package com.mincorn.capstone.domain.respository

import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeDetail
import com.mincorn.capstone.domain.model.SavedRecipe
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ImageRepository {
    suspend fun uploadImage(imageFile: File): String
}

interface RecipeRepository {
    suspend fun getRecommendations(ingredients: List<String>): List<Recipe>
    suspend fun getRecipeDetail(name: String): RecipeDetail
}

interface StorageRepository {
    fun getSavedRecipes(uid: String): Flow<List<SavedRecipe>>
    suspend fun deleteRecipe(uid: String, recipe: SavedRecipe)
    suspend fun saveRecipe(uid: String, recipe: SavedRecipe)
}