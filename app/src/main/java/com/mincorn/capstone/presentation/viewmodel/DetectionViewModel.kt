package com.mincorn.capstone.presentation.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mincorn.capstone.data.source.local.ImageAnalyzer
import com.mincorn.capstone.data.source.local.PreferenceManager
import com.mincorn.capstone.domain.respository.ImageRepository
import com.mincorn.capstone.domain.until.IngredientMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ImageRepository,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {

    private val analyzer = ImageAnalyzer(context)

    init {
        analyzer.setupClassifier()
    }
    var resultText by mutableStateOf("")
    private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var detectedName by mutableStateOf("")
    var detectedCount by mutableStateOf("")
    var detectedImageUri by mutableStateOf("")

    fun processImageAndGetRoute(photoFile: File): String {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val resultName = analyzer.analyze(bitmap)
        val category = IngredientMapper.getCategory(resultName)
        val encodedCategory = URLEncoder.encode(category, "UTF-8")

        return "typeDetail/$encodedCategory"
    }

    fun getStoredUrl(): String = preferenceManager.getNgrokUrl()

    fun updateUrl(newUrl: String) {
        preferenceManager.setNgrokUrl(newUrl)
    }

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val result = repository.uploadImage(imageFile)

                resultText = result
            } catch (e: Exception) {
                errorMessage = "업로드 실패: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}