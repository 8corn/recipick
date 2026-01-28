package com.mincorn.capstone.domain.model

data class DetectedIngredient(
    val name: String,
    val count: Int,
    val category: String,
    val imageUri: String? = null
)