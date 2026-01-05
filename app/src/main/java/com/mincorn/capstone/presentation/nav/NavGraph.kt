package com.mincorn.capstone.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mincorn.capstone.presentation.MainActivity
import com.mincorn.capstone.presentation.Reciepick

@Composable
fun NavGraph (
    startPage: String
) {
    val navController = rememberNavController()

    NavHost (
        navController = navController,
        startDestination = startPage
    ) {
        composable ("Reciepick") {
            Reciepick(navController)
        }
    }
}