package com.mincorn.capstone.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.mincorn.capstone.SavedRecipe

class StorageViewModel : ViewModel() {
    var savedRecipes = mutableStateListOf<SavedRecipe>()
        private set

    private var listenerRegistration: ListenerRegistration? = null

    init {
        observeSavedRecipes()
    }

    private fun observeSavedRecipes() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        listenerRegistration = db.collection("user")
            .document(uid)
            .collection("storage")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val loaded = snapshot.documents.mapNotNull { doc ->
                        val name = doc.getString("name")
                        val ingredients = doc.getString("ingredients")
                        val recipe = doc.getString("recipe")
                        val image = doc.getString("image") ?: ""

                        if (name != null && ingredients != null && recipe != null) {
                            SavedRecipe(name, ingredients, recipe, image)
                        } else {
                            null
                        }
                    }

                    savedRecipes.clear()
                    savedRecipes.addAll(loaded)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}