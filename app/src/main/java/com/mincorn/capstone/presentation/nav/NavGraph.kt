package com.mincorn.capstone.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mincorn.capstone.presentation.join.JoinActivity
import com.mincorn.capstone.presentation.join.LoginActivity
import com.mincorn.capstone.presentation.main.MediaPipeCheck
import com.mincorn.capstone.presentation.main.Recipick
import com.mincorn.capstone.presentation.main.TypeDetail
import com.mincorn.capstone.presentation.main.AddCamera
import com.mincorn.capstone.presentation.recipick.PickRecipick
import com.mincorn.capstone.presentation.recipick.SearchRecipick

@Composable
fun NavGraph (
    startPage: String
) {
    val navController = rememberNavController()

    NavHost (
        navController = navController,
        startDestination = startPage
    ) {
        composable ("Recipick") {
            Recipick(navController)
        }
        composable ("AddCamera") {
            AddCamera(navController)
        }
        composable ("MediaPipeConnect") {
            MediaPipeCheck(navController, "", "", "")
        }
        composable (
            route = "PickRecipick/{pickName}/{imageUrl}",
            arguments = listOf(
                navArgument("pickName") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val pickName = backStackEntry.arguments?.getString("pickName") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""

            PickRecipick(navController, pickName, imageUrl)
        }
        composable ("SearchRecipick") {
            SearchRecipick(navController)
        }
        composable ("JoinActivity") {
            JoinActivity(navController)
        }
        composable ("LoginActivity") {
            LoginActivity(navController)
        }
    }
}