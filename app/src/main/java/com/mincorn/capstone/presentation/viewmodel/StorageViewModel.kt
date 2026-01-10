package com.mincorn.capstone.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mincorn.capstone.data.source.local.PreferenceManager
import com.mincorn.capstone.domain.model.DeleteRecipeUseCase
import com.mincorn.capstone.domain.model.GetSavedRecipesUseCase
import com.mincorn.capstone.domain.model.SaveRecipeUseCase
import com.mincorn.capstone.domain.model.SavedRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase,
    private val saveRecipesUseCase: SaveRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {
    var savedRecipes = mutableStateListOf<SavedRecipe>()
        private set

    init {
        observeSavedRecipes()
    }

    fun saveRecipe(recipe: SavedRecipe) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            saveRecipesUseCase(uid, recipe)
        }
    }

    fun deleteRecipe(recipe: SavedRecipe) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            deleteRecipeUseCase(uid, recipe)
        }
    }

    private fun observeSavedRecipes() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            getSavedRecipesUseCase(uid).collect { list ->
                savedRecipes.clear()
                savedRecipes.addAll(list)
            }
        }
    }
}