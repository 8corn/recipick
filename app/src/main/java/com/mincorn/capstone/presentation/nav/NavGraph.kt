package com.mincorn.capstone.presentation.nav

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
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
import com.mincorn.capstone.presentation.recipick.StorageRecipick
import com.mincorn.capstone.presentation.viewmodel.DetectionViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph (
    startPage: String
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val detectionViewModel: DetectionViewModel = hiltViewModel(context as ComponentActivity)

    NavHost (
        navController = navController,
        startDestination = startPage
    ) {
        composable ("Recipick") {
            Recipick(navController)
        }
        composable ("SearchRecipick") {
            SearchRecipick(navController)
        }
        composable (
            route = "SearchMenu/{keyword}",
            arguments = listOf(navArgument("keyword") {type = NavType.StringType})
        ) {backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""

            val decodedKeyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8.toString())

            SearchMenu(
                navController = navController,
                keyword = decodedKeyword,
                onRecipeClick = { name, imageUrl, description ->
                    val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                    val encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                    navController.navigate("PickRecipick/$name/$encodedUrl/$encodedDesc")
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
            route = "PickRecipick/{pickName}/{imageUrl}/{description}",
            arguments = listOf(
                navArgument("pickName") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val pickName = backStackEntry.arguments?.getString("pickName") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""

            PickRecipick(navController, pickName, imageUrl, description)
        }
        composable ("AddCamera") {
            AddCamera(navController, detectionViewModel)
        }
        composable(
            route = "typeDetail/{typeName}",
            arguments = listOf(navArgument("typeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("typeName") ?: ""

            val decodedTypeName = URLDecoder.decode(typeName, StandardCharsets.UTF_8.toString())

            TypeDetail(
                navController = navController,
                onCameraClick = {
                    navController.navigate("AddCamera")
                },
                detectionViewModel = detectionViewModel,
                typeName = decodedTypeName,
            )
        }
        composable (
            route = "StorageRecipick/{name}/{image}/{ingredients}/{instructions}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType },
                navArgument("ingredients") { type = NavType.StringType },
                navArgument("instructions") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val image = backStackEntry.arguments?.getString("image") ?: ""
            val ingredients = backStackEntry.arguments?.getString("ingredients") ?: ""
            val instructions = backStackEntry.arguments?.getString("instructions") ?: ""

            StorageRecipick(navController, name, image, ingredients, instructions)
        }
    }
}