package com.mincorn.capstone.data.source.remote

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Gemini(apiKey: String) {
    private var generativeModel = GenerativeModel(
        modelName = "gemini-3-flash",
        apiKey = apiKey
    )

    suspend fun generateText(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "응답이 없습니다."
        } catch (e: Exception) {
            "에러: ${e.localizedMessage}"
        }
    }
}