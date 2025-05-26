package com.mincorn.capstone.other

import android.content.Context
import android.net.Uri
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import java.io.File

fun uploadImageToServer(imageFile: File, context: Context): String {
    val client = OkHttpClient()
    val url = AppSettings.getNgrokUrl(context) + "/upload-image/"

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
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val jsonString = response.body.string()
        val gson = com.google.gson.Gson()
        val result = gson.fromJson(jsonString, DetectionResult::class.java)

        return result.message + "\n" +
                (result.objects?.joinToString("\n") { it.label + " (" + it.confidence + ")" } ?: "")
    }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}

object AppSettings {
    private const val PREF_NAME = "settings"
    private const val KEY_NGROK_URL = "ngrok_url"

    fun getNgrokUrl(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_NGROK_URL, "") ?: ""
    }

    fun setNgrokURL(context: Context, url: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NGROK_URL, url).apply()
    }
}

data class DetectionResult(
    val message: String,
    val objects: List<DetectedObject>?
)

data class DetectedObject(
    val label: String,
    val confidence: Float,
    @SerializedName("bounding_box") val boundingBox: BoundingBox
)

data class BoundingBox(
    val xMin: Int,
    val yMin: Int,
    val width: Int,
    val height: Int
)