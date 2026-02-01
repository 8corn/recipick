package com.mincorn.capstone.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.view.WindowCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.domain.model.SavedRecipe
import com.mincorn.capstone.presentation.nav.NavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            NavGraph(
                startPage = "LoginActivity"
//                startPage = "Recipick"
            )
        }
    }
}