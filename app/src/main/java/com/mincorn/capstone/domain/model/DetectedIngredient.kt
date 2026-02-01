package com.mincorn.capstone.domain.model

data class DetectedIngredient(
    val name: String = "",
    val count: Int = 0,
    val category: String = "",
    val imageUri: String? = null,
    val createTime: Long = System.currentTimeMillis()
)