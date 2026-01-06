package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mincorn.capstone.presentation.other.BottomNavigationBar

@Composable
fun Reciepick(
    rootNavController: NavController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(bottomNavController)
            }
            composable("search") {
                SearchScreen(bottomNavController)
            }
            composable("storage") {
                StorageScreen(bottomNavController)
            }

            composable(
                route = "typeDetail/{typeName}",
                arguments = listOf(navArgument("typeName") { type = NavType.StringType })
            ) { backStackEntry ->
                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""

                TypeDetail(
                    navController = bottomNavController,
                    typeName = typeName,
                    imageUri = "",
                    name = "",
                    count = "",
                )
            }
        }
    }
}

