package com.mincorn.capstone.domain.model

data class Recipe(
    val name: String,
    val description: String,
    val imageUrl: String
)

interface RecipeRepository {
    suspend fun getRecommendations(ingredients: List<String>): List<Recipe>
}

class GetRecipeRecommendationUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> {
        return repository.getRecommendations(ingredients)
    }
}