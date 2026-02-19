package com.mincorn.capstone.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mincorn.capstone.data.source.remote.Gemini
import com.mincorn.capstone.data.source.remote.UnsplashApi
import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeDetail
import com.mincorn.capstone.domain.respository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Named

class RecipeRepositoryImpl @Inject constructor(
    private val geminiDataSource: Gemini,
    private val api: UnsplashApi,
    @Named("unsplash_key") private val accessKey: String,
): RecipeRepository {
    override suspend fun getRecommendations(ingredients: List<String>): List<Recipe> {
        val prompt = """
            사용자가 가진 재료: ${ingredients.joinToString(", ")}
            
            [핵심 규칙]
            1. 위 재료를 '모두' 사용할 필요는 없습니다. 
            2. 주어진 재료 중 서로 잘 어울리는 것들만 선택하여 대중적이고 맛있는 요리를 추천해줘.
            3. 요리와 전혀 어울리지 않는 재료는 과감히 제외하고 상식적인 수준에서 레시피를 구성해.
            4. 기본 양념(소금, 설탕, 간장 등)은 집에 있다고 가정해도 좋아.
            
            위 재료로 만들 수 있는 요리를 10개 이상 추천해줘.
            응답은 반드시 아래와 같은 JSON 형식의 리스트로만 답변해줘. 다른 설명은 하지마.
            각 요리의 'translatedName'은 Unsplash에서 음식 사진을 검색할 때 사용할 영어 키워드여야 해.
    
            JSON 형식 예시:
            [
              {
                "name": "요리 이름",
                "description": "요리에 대한 짧고 맛깔나는 1줄 설명",
                "translatedName": "English food name for image search"
              }
            ]
        """.trimIndent()

        val response = geminiDataSource.generateText(prompt)

        return parseRecipes(response)
    }

    override suspend fun getRecipeDetail(name: String): RecipeDetail {
        val prompt = """
            $name 요리의 레시피를 알려줘.
            응답은 반드시 아래 JSON 형식으로만 답변해줘. 다른 설명은 하지마.
            
            규칙:
            1. 'instructions'는 각 단계를 "1번", "2번"과 같이 시작해.
            2. 각 단계가 끝나면 반드시 줄바꿈 문자(\n)를 넣어줘.
            3. 만약 특정 단계가 선택 사항이라면 "2번(선택 사항)" 처럼 번호 바로 옆에 붙여줘.
            
            예시:
            {
              "ingredients": "재료1, 재료2, 재료3",
              "instructions": "1번\n양파를 썹니다.\n2번(선택 사항)\n청양고추를 넣습니다."
            }
        """.trimIndent()

        val response = geminiDataSource.generateText(prompt)

        return try {
            val cleanJson = response.replace("```json", "").replace("```", "").trim()
            val dto = Gson().fromJson(cleanJson, RecipeDetailDto::class.java)
            RecipeDetail(dto.ingredients, dto.instructions)
        } catch (e: Exception) {
            RecipeDetail("재료를 불러올 수 없습니다.", "레시피를 불러올 수 없습니다.")
        }
    }

    override suspend fun getRecipesByKeyword(keyword: String): List<Recipe> {
        val prompt = """
            사용자가 검색한 요리: $keyword
            
            '$keyword'와 관련하여 조리법이나 스타일이 다른 다양한 레시피 10개 이상 추천해줘.
            예를 들어 사용자가 '갈비찜'을 검색했다면, '매운 갈비찜', '궁중 갈비찜', '백종원표 갈비찜' 등 구체적이고 서로 다른 특징을 가진 메뉴 이름을 정해줘.
            
            응답은 반드시 아래와 같은 JSON 형식의 리스트로만 답변해줘. 다른 설명은 하지마.
            'translatedName'은 Unsplash에서 음식 사진을 검색할 때 사용할 영어 키워드여야 해 (음식 특징이 잘 드러나게).
    
            JSON 형식 예시:
            [
              {
                "name": "구체적인 요리 이름 (예: 매콤 칼칼한 매운 갈비찜)",
                "description": "이 레시피만의 특징을 살린 1줄 설명",
                "translatedName": "Spicy braised short ribs"
              }
            ]
        """.trimIndent()

        val response = geminiDataSource.generateText(prompt)
        return parseRecipes(response)
    }

    private suspend fun parseRecipes(jsonResponse: String): List<Recipe> {
        return try {
            val startIndex = jsonResponse.indexOf('[')
            val endIndex = jsonResponse.indexOf(']')

            if (startIndex == -1 || endIndex == -1) {
                Log.e("RecipeRepository", "JSON 형식을 찾을 수 없음: $jsonResponse")
                return emptyList()
            }

            val jsonString = jsonResponse.substring(startIndex, endIndex + 1)

            val type = object : TypeToken<List<RecipeDto>>() {}.type
            val recipeDtos: List<RecipeDto> = Gson().fromJson(jsonString, type)

            recipeDtos.map { dto ->
                val imageUrl = getImageUrl(dto.translatedName)
                Recipe(dto.name, dto.description, imageUrl)
            }
        } catch (e: Exception) {
            Log.e("SearchPrompt", "JSON 파싱 에러 발생: ${e.localizedMessage}")
            emptyList()
        }
    }

    private suspend fun getImageUrl(englishName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val searchQuery = "$englishName food dish -person -people -portrait"
                val encodedName = URLEncoder.encode(searchQuery, "UTF-8")

                val response = api.searchPhotos(encodedName, accessKey)
                response.results.randomOrNull()?.urls?.small ?: ""
            } catch (e: Exception) {
                Log.e("SearchViewModel", "이미지 불러오기 실패: $englishName", e)
                ""
            }
        }
    }
}

private data class RecipeDto(
    val name: String,
    val description: String,
    val translatedName: String
)

private data class RecipeDetailDto(
    val ingredients: String,
    val instructions: String
)