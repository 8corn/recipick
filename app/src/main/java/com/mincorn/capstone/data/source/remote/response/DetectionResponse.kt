package com.mincorn.capstone.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DetectionResponse(
    val message: String,
    val objects: List<DetectedObjectDto>?
)

data class DetectedObjectDto(
    val label: String,
    val confidence: Float,
    @SerializedName("bounding_box") val boundingBox: BoundingBoxDto
)

data class BoundingBoxDto(
    val xMin: Int,
    val yMin: Int,
    val width: Int,
    val height: Int
)