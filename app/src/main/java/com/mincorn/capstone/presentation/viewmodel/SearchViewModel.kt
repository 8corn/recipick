package com.mincorn.capstone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.data.datastore.SearchDataStore
import com.mincorn.capstone.domain.model.GetRecipeDetailUseCase
import com.mincorn.capstone.domain.model.GetRecipeRecommendationUseCase
import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeDetail
import com.mincorn.capstone.domain.respository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getRecipeRecommendationUseCase: GetRecipeRecommendationUseCase,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val searchDataStore: SearchDataStore,
    private val repository: RecipeRepository,
) : ViewModel() {
    var detail by mutableStateOf<RecipeDetail?>(null)
    var isLoading by mutableStateOf(false)

    var recipes = mutableStateListOf<Recipe>()
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    val recentSearches: StateFlow<List<String>> = searchDataStore.getRecentSearches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults.asStateFlow()



    fun searchRecipes(keyword: String) {
        viewModelScope.launch {
            val results = repository.getRecipesByKeyword(keyword)
            _searchResults.value = results
        }
    }

    fun addSearchKeyword(keyword: String) {
        val cleanKeyword = keyword.trim()
        if (cleanKeyword.isBlank()) return

        viewModelScope.launch {
            searchDataStore.saveSearch(cleanKeyword)
        }
    }

    fun removeSearchKeyword(keyword: String) {
        viewModelScope.launch {
            searchDataStore.deleteSearch(keyword)
        }
    }

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