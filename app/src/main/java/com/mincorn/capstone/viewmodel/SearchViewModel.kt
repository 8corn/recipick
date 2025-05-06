package com.mincorn.capstone.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.Recipe
import com.mincorn.capstone.main.Gemini
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
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
                    recipes.add(Recipe(parts[0], parts[1], com.mincorn.capstone.R.drawable.vmon))
                }
            }
            isRefreshing = false
        }
    }
}