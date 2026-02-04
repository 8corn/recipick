package com.mincorn.capstone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.domain.respository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {
    override suspend fun saveUser(uid: String, aka: String, email: String, provider: String) {
        val user = hashMapOf(
            "aka" to aka,
            "email" to email,
            "provider" to provider
        )
        firestore.collection("user").document(uid).set(user).await()
    }

    override suspend fun isUserExists(email: String): String? {
        val result = firestore.collection("user")
            .whereEqualTo("email", email)
            .get()
            .await()
        return if (!result.isEmpty) result.documents[0].id else null
    }
}