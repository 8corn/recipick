package com.mincorn.capstone.data.repository

import android.util.Log
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
        var baseUrl = preferenceManager.getNgrokUrl().trim().removeSuffix("/")

        if (!baseUrl.startsWith("http")) {
            baseUrl = "https://$baseUrl"
        }

        val url = "$baseUrl/upload-image/"
        Log.d("ImageRepository", "업로드 URL: $url")

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
            if (!response.isSuccessful) throw IOException("서버 응답 에러 발생: $response")

            val finalImageUrl = "$baseUrl/static/${imageFile.name}"
            Log.d("ImageRepository", "저장된 이미지 경로: $finalImageUrl")

            return@withContext finalImageUrl
        }
    }
}