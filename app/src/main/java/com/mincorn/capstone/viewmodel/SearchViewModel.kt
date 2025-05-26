package com.mincorn.capstone.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.R
import com.mincorn.capstone.Recipe
import com.mincorn.capstone.main.Gemini
import com.mincorn.capstone.other.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val accessKey = application.getString(R.string.unsplash_access_key)

    var recipes = mutableStateListOf<Recipe>()
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    private val ingredients = listOf("돼지고기", "양파", "고추장")

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            isRefreshing = true

            val prompt = """
             재료 : ${ingredients.joinToString { ", " }}
             
             이 재료들을 가지고 요리를 추천 해줘
             근데 각 요리는 다음과 같은 형식으로 알려줘:
            
             [요리 이름] :  [간단한 설명]
             
             예)
             김치 볶음밥: 김치와 밥을 볶아 만든 기본적인 대표 볶음밥
             된장찌개: 한국인 최고의 찌개
  
             이런 식으로 총 5개 이상 요리를 간단하게 추천해줘.
            """.trimIndent()

            val response = Gemini.generateText(prompt)
            val lines = response.lines().filter { it.contains(":") }

            recipes.clear()
            for (line in lines) {
                val parts = line.split(":").map { it.trim() }
                if (parts.size == 2) {
                    val name = parts[0]
                    val description = parts[1]

                    val translatePrompt = """
                        "$name" 이라는 요리 이름을 영어 단어로만 짧고 간단하게 번역해줘.
                        예) 김치찌개 -> kimchi stew
                        예) 제육볶음 -> spicy pork stir-fry
                        결과는 영어로만 출력해줘.
                    """.trimIndent()

                    val translatedName = Gemini.generateText(translatePrompt).trim()
                    Log.d("SearchViewModel", "영어 번역 결과: $translatedName")

                    val imageUrl = try {
                        val searchQuery = "$translatedName food dish -person -people -portrait"
                        val encodedName = URLEncoder.encode(searchQuery, "UTF-8")
                        val unsplashResponse = withContext(Dispatchers.IO) {
                            RetrofitInstance.api.searchPhotos(encodedName, accessKey)
                        }
                        val randomImage = unsplashResponse.results.randomOrNull()
                        randomImage?.urls?.small ?: ""

                    } catch (e: Exception) {
                        Log.e("SearchViewModel", "이미지 불러오기 실패", e)
                        ""
                    }

                    recipes.add(Recipe(name, description, imageUrl))
                }
            }
            isRefreshing = false
        }
    }
}