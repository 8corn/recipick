package com.mincorn.capstone.main

import com.google.ai.client.generativeai.GenerativeModel
import android.content.Context
import com.mincorn.capstone.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Gemini {
    private lateinit var apiKey: String

    fun init(context: Context) {
        apiKey = context.getString(R.string.gemini)
    }

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel (
            modelName = "gemini-pro",
            apiKey = apiKey,
        )
    }

    suspend fun generateText(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text ?: "응답이 없습니다."
            } catch (e: Exception) {
                "에러: ${e.localizedMessage}"
            }
        }
    }
}