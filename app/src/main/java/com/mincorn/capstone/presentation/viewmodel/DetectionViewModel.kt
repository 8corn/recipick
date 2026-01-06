package com.mincorn.capstone.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mincorn.capstone.domain.respository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DetectionViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {
    var resultText by mutableStateOf("")
    private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

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