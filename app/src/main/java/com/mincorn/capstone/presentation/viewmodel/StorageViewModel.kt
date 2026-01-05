package com.mincorn.capstone.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mincorn.capstone.domain.model.GetSavedRecipesUseCase
import com.mincorn.capstone.domain.model.SavedRecipe
import com.mincorn.capstone.domain.model.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase,
    private val repository: StorageRepository,
) : ViewModel() {
    var savedRecipes = mutableStateListOf<SavedRecipe>()
        private set

    fun deleteRecipe(recipe: SavedRecipe) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            repository.deleteRecipe(uid, recipe)
        }
    }

    private fun observeSavedRecipes() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            getSavedRecipesUseCase(uid).collect { list ->
                savedRecipes.clear()
                savedRecipes.addAll(list)
            }
        }
    }
}