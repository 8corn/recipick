package com.mincorn.capstone.viewmodel

import android.app.Application
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

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    val accessKey = application.getString(R.string.unsplash_access_key)

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

                    val imageUrl = try {
                        val unsplashResponse = withContext(Dispatchers.IO) {
                            RetrofitInstance.api.searchPhotos(name, accessKey)
                        }
                        unsplashResponse.results.firstOrNull()?.urls?.small ?: ""
                    } catch (e: Exception) {
                        print("이미지 불러오기 실해 $e")
                        ""
                    }

                    recipes.add(Recipe(name, description, imageUrl))
                }
            }
            isRefreshing = false
        }
    }
}