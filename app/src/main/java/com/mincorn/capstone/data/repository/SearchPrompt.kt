package com.mincorn.capstone.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeRepository
import com.mincorn.capstone.data.source.remote.Gemini
import com.mincorn.capstone.data.source.remote.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val geminiDataSource: Gemini,
    private val api: UnsplashApi,
    private val accessKey: String,
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

    private suspend fun parseRecipes(jsonResponse: String): List<Recipe> {
        return try {
            val cleanJson = jsonResponse
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val type = object : TypeToken<List<RecipeDto>>() {}.type
            val recipeDtos: List<RecipeDto> = Gson().fromJson(cleanJson, type)

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