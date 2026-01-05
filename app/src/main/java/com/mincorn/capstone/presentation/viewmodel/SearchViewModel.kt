package com.mincorn.capstone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.domain.model.GetRecipeRecommendationUseCase
import com.mincorn.capstone.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getRecipeRecommendationUseCase: GetRecipeRecommendationUseCase
) : ViewModel() {
    var recipes = mutableStateListOf<Recipe>()
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    private val ingredients = listOf("돼지고기", "양파", "고추장")

    fun loadRecipes() {
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