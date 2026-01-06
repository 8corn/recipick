package com.mincorn.capstone.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mincorn.capstone.presentation.join.JoinActivity
import com.mincorn.capstone.presentation.join.LoginActivity
import com.mincorn.capstone.presentation.main.HomeScreen
import com.mincorn.capstone.presentation.main.MediaPipeCheck
import com.mincorn.capstone.presentation.main.Reciepick
import com.mincorn.capstone.presentation.main.SearchScreen
import com.mincorn.capstone.presentation.main.StorageScreen
import com.mincorn.capstone.presentation.main.TypeDetail
import com.mincorn.capstone.presentation.main.AddCamera
import com.mincorn.capstone.presentation.recipick.PickReciepick
import com.mincorn.capstone.presentation.recipick.SearchReciepick

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
        composable ("AddCamera") {
            AddCamera(navController)
        }
        composable ("MediaPipeConnect") {
            MediaPipeCheck(navController, "", "", "")
        }
        composable (
            route = "PickReciepick/{pickName}/{imageUrl}",
            arguments = listOf(
                navArgument("pickName") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val pickName = backStackEntry.arguments?.getString("pickName") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            PickReciepick(navController, pickName, imageUrl)
        }
        composable ("SearchReciepick") {
            SearchReciepick(navController)
        }
        composable ("JoinActivity") {
            JoinActivity(navController)
        }
        composable ("LoginActivity") {
            LoginActivity(navController)
        }
        composable ("TypeDetail") {
            TypeDetail(navController, "", "", "", "")
        }
    }
}