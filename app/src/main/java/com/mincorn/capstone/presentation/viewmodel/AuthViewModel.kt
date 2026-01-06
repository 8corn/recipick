package com.mincorn.capstone.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.domain.model.GetSavedRecipesUseCase
import com.mincorn.capstone.domain.model.SaveRecipeUseCase
import com.mincorn.capstone.domain.model.SavedRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val savedRecipesUseCase: SaveRecipeUseCase,
    private val getSavedRecipesUseCase: GetSavedRecipesUseCase
) : ViewModel() {

    fun saveRecipe(recipe: SavedRecipe) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            savedRecipesUseCase.invoke(uid, recipe)
        }
    }
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    var loginSuccess by mutableStateOf(false)
        private set

    fun signUpWithEmail(aka: String, email: String, pw: String) {
        auth.createUserWithEmailAndPassword(email, pw)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val user = hashMapOf(
                        "aka" to aka,
                        "email" to email,
                        "provider" to "email"
                    )

                    db.collection("user").document(uid).set(user)
                        .addOnSuccessListener {
                            loginSuccess = true
                        }
                }
            }
    }

    fun saveSocialUser(
        aka: String,
        email: String,
        provider: String
    ) {
        db.collection("user")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                val uid = if (!result.isEmpty) {
                    result.documents[0].id
                } else {
                    auth.currentUser?.uid
                }

                if (uid != null) {
                    val user = hashMapOf(
                        "aka" to aka,
                        "email" to email,
                        "provider" to provider
                    )

                    db.collection("user").document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            loginSuccess = true
                        }
                }
            }
    }
}