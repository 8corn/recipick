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
            
            위 재료로 만들 수 있는 요리를 5개 이상 추천해줘.
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
            {
              "ingredients": "필요한 재료들을 쉼표로 구분한 문자열",
              "instructions": "요리 순서를 1. 2. 3. 번호를 붙여 설명한 문자열"
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