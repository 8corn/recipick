package com.mincorn.capstone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.domain.model.DetectedIngredient
import com.mincorn.capstone.domain.model.SavedRecipe
import com.mincorn.capstone.domain.respository.StorageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StorageRepository {
    override fun getSavedRecipes(uid: String): Flow<List<SavedRecipe>> = callbackFlow {
        val subscription = firestore.collection("user")
            .document(uid)
            .collection("storage")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val recipes = snapshot?.documents?.mapNotNull { doc ->
                    SavedRecipe(
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        ingredients = doc.getString("ingredients") ?: "",
                        recipe = doc.getString("recipe") ?: "",
                        image = doc.getString("image") ?: "",
                    )
                } ?: emptyList()
                trySend(recipes)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveRecipe(uid: String, recipe: SavedRecipe) {
        try {
            firestore.collection("user")
                .document(uid)
                .collection("storage")
                .add(recipe)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteRecipe(uid: String, recipe: SavedRecipe) {
        try {
            val collection = firestore.collection("user").document(uid).collection("storage")
            val snapshot = collection.get().await()

            val targetDoc = snapshot.documents.find { doc ->
                doc.getString("name") == recipe.name && doc.getString("ingredients") == recipe.ingredients
            }

            targetDoc?.reference?.delete()?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getIngredients(uid: String): Flow<List<DetectedIngredient>> = callbackFlow {
        val subscription = firestore.collection("user")
            .document(uid)
            .collection("ingredients")
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(DetectedIngredient::class.java)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveIngredient(uid: String, ingredient: DetectedIngredient) {
        firestore.collection("user")
            .document(uid)
            .collection("ingredients")
            .document(ingredient.name)
            .set(ingredient)
            .await()
    }
}

