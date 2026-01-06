package com.mincorn.capstone.domain.model

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SavedRecipe(
    val name: String,
    val ingredients: String,
    val recipe: String,
    val image: String,
)

interface StorageRepository {
    fun getSavedRecipes(uid: String): Flow<List<SavedRecipe>>
    suspend fun deleteRecipe(uid: String, recipe: SavedRecipe)
    suspend fun saveRecipe(uid: String, recipe: SavedRecipe)
}

class GetSavedRecipesUseCase @Inject constructor(
    private val repository: StorageRepository
) {
    operator fun invoke(uid: String): Flow<List<SavedRecipe>> {
        return repository.getSavedRecipes(uid)
    }
}

class SaveRecipeUseCase @Inject constructor(
    private val repository: StorageRepository
) {
    suspend operator fun invoke(uid: String, recipe: SavedRecipe) {
        repository.saveRecipe(uid, recipe)
    }
}