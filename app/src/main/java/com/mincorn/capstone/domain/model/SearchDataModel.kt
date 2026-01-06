package com.mincorn.capstone.domain.model

import javax.inject.Inject

data class Recipe(
    val name: String,
    val description: String,
    val imageUrl: String
)

data class RecipeDetail(
    val ingredients: String,
    val instructions: String
)

interface RecipeRepository {
    suspend fun getRecommendations(ingredients: List<String>): List<Recipe>

    suspend fun getRecipeDetail(name: String): RecipeDetail
}

class GetRecipeRecommendationUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> {
        return repository.getRecommendations(ingredients)
    }
}

class GetRecipeDetailUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(name: String): RecipeDetail {
        return repository.getRecipeDetail(name)
    }
}