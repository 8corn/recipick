package com.mincorn.capstone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.domain.model.GetRecipeDetailUseCase
import com.mincorn.capstone.domain.model.GetRecipeRecommendationUseCase
import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getRecipeRecommendationUseCase: GetRecipeRecommendationUseCase,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
) : ViewModel() {
    var detail by mutableStateOf<RecipeDetail?>(null)
    var isLoading by mutableStateOf(false)

    var recipes = mutableStateListOf<Recipe>()
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    fun loadDetailRecipe(name: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                detail = getRecipeDetailUseCase(name)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "상세 정보 로드 실패", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun loadRecipes(ingredients: List<String>) {
        viewModelScope.launch {
            isRefreshing = true

            try {
                val result = getRecipeRecommendationUseCase(ingredients)
                recipes.clear()
                recipes.addAll(result)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error", e)
            } finally {
                isRefreshing = false
            }
        }
    }
}