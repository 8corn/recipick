package com.mincorn.capstone.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mincorn.capstone.domain.respository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {
    var loginSuccess by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun clearError() { errorMessage = null }

    init {
        if (auth.currentUser != null) {
            loginSuccess = true
        }
    }

    fun signUpWithEmail(aka: String, email: String, pw: String) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    viewModelScope.launch {
                        try {
                            userRepository.saveUser(uid, aka, email, "email")
                            loginSuccess = true
                        } catch (e: Exception) {
                            errorMessage = "사용자 정보 저장 중 오류가 발생하였습니다: ${e.message}"
                        }
                    }
                } else {
                    errorMessage = task.exception?.message ?: "회원가입 실패"
                }
            }
    }

    fun saveSocialUser(
        aka: String,
        email: String,
        provider: String
    ) {
        viewModelScope.launch {
            try {
                val existingUid = userRepository.isUserExists(email)
                val uid = existingUid ?: auth.currentUser?.uid

                if (uid != null) {
                    userRepository.saveUser(uid, aka, email, provider)
                    loginSuccess = true
                } else {
                    errorMessage = "로그인 정보를 찾을 수 없습니다."
                }
            } catch (e: Exception) {
                errorMessage = "$provider 로그인 처리 중 오류 발생: ${e.message}"
            }
        }
    }
}