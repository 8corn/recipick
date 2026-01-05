package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.HomeScreen
import com.mincorn.capstone.presentation.NavigationItem
import com.mincorn.capstone.presentation.SearchScreen
import com.mincorn.capstone.presentation.StorageScreen

@Composable
fun Reciepick(
    navController: NavController
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
            composable("storage") { StorageScreen() }
            composable("typeDetail/{typeName}/{imageUri}/{name}/{count}") { backStackEntry ->
                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""
                val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val count = backStackEntry.arguments?.getString("count") ?: ""
                TypeDetailScreen(
                    typeName = typeName,
                    imageUri = imageUri,
                    name = name,
                    count = count,
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", R.drawable.chili, "홈"),
        NavigationItem("search", R.drawable.garlic_icon, "검색"),
        NavigationItem("storage", R.drawable.greenonion_icon, "저장"),
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column {
        HorizontalDivider(
            color = Color(0xFF868686),
            thickness = 1.dp,
        )

        NavigationBar(
            containerColor = Color.White
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 16.sp,
                            color = if (isSelected) Color.Black else Color(0xFFDEDEDE)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route)
                        }
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                    ),
                )
            }
        }
    }
}