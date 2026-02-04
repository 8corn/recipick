package com.mincorn.capstone.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.mincorn.capstone.domain.respository.ImageRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class ImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
): ImageRepository {
    override suspend fun uploadImage(imageFile: File): String = suspendCancellableCoroutine{ continuation ->
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val storageRef = storage.reference.child("ingredients/$fileName")

        val uploadTask = storageRef.putFile(Uri.fromFile(imageFile))

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                continuation.resume(downloadUri) {

                }
            } else {
                continuation.resumeWithException(task.exception ?: Exception("업로드 실패"))
            }
        }
    }
}