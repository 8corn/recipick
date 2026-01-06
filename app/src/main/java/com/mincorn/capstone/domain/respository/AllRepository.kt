package com.mincorn.capstone.domain.respository

import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeDetail
import java.io.File

interface ImageRepository {
    suspend fun uploadImage(imageFile: File): String
}

interface RecipeRepository {
    suspend fun getRecommendations(ingredients: List<String>): List<Recipe>
    suspend fun getRecipeDetail(name: String): RecipeDetail
}