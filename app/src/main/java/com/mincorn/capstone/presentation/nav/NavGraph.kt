package com.mincorn.capstone.presentation.nav

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mincorn.capstone.presentation.join.JoinActivity
import com.mincorn.capstone.presentation.join.LoginActivity
import com.mincorn.capstone.presentation.main.AddCamera
import com.mincorn.capstone.presentation.main.MediaPipeCheck
import com.mincorn.capstone.presentation.main.Recipick
import com.mincorn.capstone.presentation.main.SearchMenu
import com.mincorn.capstone.presentation.main.TypeDetail
import com.mincorn.capstone.presentation.recipick.PickRecipick
import com.mincorn.capstone.presentation.recipick.SearchRecipick
import com.mincorn.capstone.presentation.viewmodel.DetectionViewModel
import com.mincorn.capstone.presentation.viewmodel.SearchViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph (
    startPage: String
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val detectionViewModel: DetectionViewModel = hiltViewModel(context as ComponentActivity)
    val searchViewModel: SearchViewModel = hiltViewModel(context as ComponentActivity)

    NavHost (
        navController = navController,
        startDestination = startPage
    ) {
        composable ("Recipick") {
            Recipick(navController, detectionViewModel)
        }
        composable ("SearchRecipick") {
            SearchRecipick(navController)
        }
        composable (
            route = "SearchMenu/{keyword}",
            arguments = listOf(navArgument("keyword") {type = NavType.StringType})
        ) {backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
            SearchMenu(
                navController = navController,
                keyword = keyword,
                onRecipeClick = { name, imageUrl ->
                    val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("PickRecipick/$name/$encodedUrl")
                }
            )
        }
        composable ("JoinActivity") {
            JoinActivity(navController)
        }
        composable ("LoginActivity") {
            LoginActivity(navController)
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
        composable ("AddCamera") {
            AddCamera(navController, detectionViewModel)
        }
        composable(
            route = "typeDetail/{typeName}",
            arguments = listOf(navArgument("typeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("typeName") ?: ""

            TypeDetail(
                navController = navController,
                onCameraClick = {
                    navController.navigate("AddCamera")
                },
                detectionViewModel = detectionViewModel,
                typeName = typeName,
            )
        }
    }
}