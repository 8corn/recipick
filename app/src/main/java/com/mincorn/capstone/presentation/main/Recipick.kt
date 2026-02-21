package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mincorn.capstone.presentation.other.BottomNavigationBar
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun Recipick(
    navController: NavController,
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            composable("home") {
                HomeScreen(
                    navController = navController,
                    onCameraClick = {
                        navController.navigate("AddCamera")
                    }
                )
            }
            composable("search") {
                SearchScreen(
                    navController = navController,
                    onRecipeClick = { name, imageUrl, description ->
                        val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                        val encodedDesc = URLEncoder.encode(description, StandardCharsets.UTF_8.toString())
                        navController.navigate("PickRecipick/$name/$encodedUrl/$encodedDesc")
                    }
                )
            }
            composable("storage") {
                StorageScreen(navController)
            }
        }
    }
}

