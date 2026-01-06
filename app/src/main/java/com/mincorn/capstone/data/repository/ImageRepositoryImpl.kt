package com.mincorn.capstone.data.repository

import androidx.camera.core.ImageProcessor
import com.google.gson.Gson
import com.mincorn.capstone.data.source.local.PreferenceManager
import com.mincorn.capstone.data.source.remote.response.DetectionResponse
import com.mincorn.capstone.domain.respository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val client: OkHttpClient,
    private val preferenceManager: PreferenceManager
): ImageRepository {
    override suspend fun uploadImage(imageFile: File): String = withContext(Dispatchers.IO){
        val url = preferenceManager.getNgrokUrl() + "/upload-image/"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", imageFile.name,
                imageFile.asRequestBody("image/*".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("에러 발생: $response")

            val jsonString = response.body?.string() ?: ""
            val result = Gson().fromJson(jsonString, DetectionResponse::class.java)

            val objectList = result.objects?.joinToString("\n") {
                "${it.label} (${(it.confidence * 100).toInt()}%)"
            } ?: "검색된 결과가 없습니다."

            "${result.message}\n\n[탐지된 자료]\n$objectList"
        }
    }
}