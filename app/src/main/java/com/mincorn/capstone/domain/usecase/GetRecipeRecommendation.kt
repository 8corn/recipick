package com.mincorn.capstone.domain.usecase

import com.mincorn.capstone.domain.model.Recipe
import com.mincorn.capstone.domain.model.RecipeRepository
import javax.inject.Inject

class GetRecipeRecommendationUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> {
        return repository.getRecommendations(ingredients)
    }
}