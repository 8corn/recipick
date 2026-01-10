package com.mincorn.capstone.domain.model

import com.mincorn.capstone.domain.respository.RecipeRepository
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