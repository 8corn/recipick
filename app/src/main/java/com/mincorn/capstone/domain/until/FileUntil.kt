package com.mincorn.capstone.domain.until

import android.content.Context
import android.net.Uri
import java.io.File

object FileUntil {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }
}

object IngredientMapper {
    fun getCategory(ingredientName: String): String {
        return when {
            ingredientName.containsAny("돼지", "소", "닭", "계란", "고기") -> "정육/계란"
            ingredientName.containsAny("당근", "파", "양파", "마늘", "배추", "무") -> "채소"
            ingredientName.containsAny("사과", "배", "포도", "바나나", "수박") -> "과일"
            ingredientName.containsAny("생선", "고등어", "갈치", "새우", "조개") -> "수산"
            else -> "기타"
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }
}