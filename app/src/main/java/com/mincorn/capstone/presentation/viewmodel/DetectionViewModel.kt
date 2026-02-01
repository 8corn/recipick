package com.mincorn.capstone.presentation.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mincorn.capstone.data.repository.PROMPT_TEXT
import com.mincorn.capstone.data.source.local.ImageAnalyzer
import com.mincorn.capstone.data.source.local.PreferenceManager
import com.mincorn.capstone.data.source.remote.Gemini
import com.mincorn.capstone.domain.model.DetectedIngredient
import com.mincorn.capstone.domain.respository.ImageRepository
import com.mincorn.capstone.domain.respository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferenceManager: PreferenceManager,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageRepository: StorageRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val analyzer = ImageAnalyzer(context)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var detectedIngredient by mutableStateOf<List<DetectedIngredient>>(emptyList())
        private set

    var currentImage by mutableStateOf<String?>(null)
        private set


    var fridgeIngredients = mutableStateListOf<DetectedIngredient>()
        private set

    private val gson = Gson()


    fun prepareAi() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            analyzer.setupClassifier()
        }
    }

    fun getStoredUrl(): String = preferenceManager.getNgrokUrl()

    fun updateUrl(newUrl: String) {
        preferenceManager.setNgrokUrl(newUrl)
    }

    fun analyzeWithGemini(apiKey: String, photoFile: File, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                currentImage = photoFile.absolutePath

                Log.d("Gemini", "1. 분석 시작")
                isLoading = true

                val result = withContext(Dispatchers.IO) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    Log.d("Gemini", "2. 비트맵 변환 완료: ${bitmap != null}")

                    val gemini = Gemini(apiKey)
                    val jsonResult = gemini.generateText(PROMPT_TEXT, bitmap)
                    Log.d("Gemini", "3. Gemini 응답 수신: $jsonResult")

                    val parse = parseJsonToIngredients(jsonResult)

                    parse.forEach { item ->
                        Log.d("Gemini_result", "분석된 재료: 이름: ${item.name}, 카테고리: ${item.category}, 개수: ${item.count}")
                    }

                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val uploadedUrl = imageRepository.uploadImage(photoFile)

                        parse.forEach { ingredient ->
                            val finalItem = ingredient.copy(
                                imageUri = uploadedUrl
                            )
                            storageRepository.saveIngredient(uid, finalItem)
                        }
                    }
                    parse
                }


                detectedIngredient = result
                isLoading = false

                loadMyFridge()

                val firstCategory = result.firstOrNull()?.category ?: "기타"
                onComplete("typeDetail/$firstCategory")
                Log.d("Gemini", "5. 화면 이동 명령 전송")

            } catch (e: Exception) {
                Log.e("Gemini", "분석/저장 실패", e)
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

    fun loadMyFridge() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                storageRepository.getIngredients(uid).collect { list ->
                    fridgeIngredients.clear()
                    fridgeIngredients.addAll(list)
                    Log.d("DetectionVM/Firebase", "냉장고 갱신 완료: ${list.size}개")
                }
            } catch (e: Exception) {
                Log.e("DetectionVM/Firebase", "재료 로드 실패", e)
            }
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