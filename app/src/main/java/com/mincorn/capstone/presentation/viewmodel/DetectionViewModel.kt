package com.mincorn.capstone.presentation.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mincorn.capstone.data.repository.PROMPT_TEXT
import com.mincorn.capstone.data.source.local.ImageAnalyzer
import com.mincorn.capstone.data.source.local.PreferenceManager
import com.mincorn.capstone.data.source.remote.Gemini
import com.mincorn.capstone.domain.model.DetectedIngredient
import com.mincorn.capstone.domain.respository.ImageRepository
import com.mincorn.capstone.domain.until.IngredientMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ImageRepository,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {

    private val analyzer = ImageAnalyzer(context)

//    init {
//        analyzer.setupClassifier()
//    }
    var resultText by mutableStateOf("")
    private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var detectedName by mutableStateOf("")
    var detectedCount by mutableStateOf("")
    var detectedImageUri by mutableStateOf("")

    var detectedIngredient by mutableStateOf<List<DetectedIngredient>>(emptyList())
        private set

    private val gson = Gson()


    fun prepareAi() {
        viewModelScope.launch {
            delay(1000)
            analyzer.setupClassifier()
        }
    }

    fun processImageAndGetRoute(photoFile: File): String {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val resultName = analyzer.analyze(bitmap)
        val category = IngredientMapper.getCategory(resultName)

        return if (category.isEmpty()) "" else "typeDetail/$category"
    }

    fun getStoredUrl(): String = preferenceManager.getNgrokUrl()

    fun updateUrl(newUrl: String) {
        preferenceManager.setNgrokUrl(newUrl)
    }

    fun analyzeWithGemini(apiKey: String) {
        viewModelScope.launch {
            try {
                isLoading = true

                val gemini = Gemini(apiKey)
                val jsonResult = gemini.generateText(PROMPT_TEXT)

                val ingredients = parseJsonToIngredients(jsonResult)

                detectedIngredient = ingredients
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Gemini 분석 실패: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    private fun parseJsonToIngredients(jsonString: String): List<DetectedIngredient> {
        return try {
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
            val itemType = object : TypeToken<List<DetectedIngredient>>() {}.type
            gson.fromJson(cleanJson, itemType)
        } catch (e: Exception) {
            emptyList()
        }
    }


    // Test
    var detectedItems by mutableStateOf<List<String>>(emptyList())
    fun fetchFakeResult() {
        viewModelScope.launch {
            val result = repository.uploadImage(File(""))

            detectedItems = result.split(",")
        }
    }

//    // 서버 연결 시 서버에 저장하는 함수
//    fun uploadImage(imageFile: File) {
//        viewModelScope.launch {
//            try {
//                isLoading = true
//                errorMessage = null
//
//                val result = repository.uploadImage(imageFile)
//
//                resultText = result
//            } catch (e: Exception) {
//                errorMessage = "업로드 실패: ${e.localizedMessage}"
//            } finally {
//                isLoading = false
//            }
//        }
//    }
}