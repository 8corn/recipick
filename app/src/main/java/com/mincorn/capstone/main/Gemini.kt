package com.mincorn.capstone.main

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.mincorn.capstone.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Gemini {
    private lateinit var generativeModel: GenerativeModel

    fun init(context: Context) {
        val apiKey = context.getString(R.string.gemini)
        generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = apiKey
        )
    }

    suspend fun generateText(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text ?: "응답이 없습니다."
            } catch (e: Exception) {
                Log.e("GeminiError", "Gemini 호출 에러", e)
                "에러: ${e.localizedMessage}"
            }
        }
    }
}