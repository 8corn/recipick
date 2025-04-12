package com.mincorn.capstone

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mincorn.capstone.main.TypeDetailScreen
import com.mincorn.capstone.recipick.PickRecipick

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reciepick()
        }
    }
}

@Composable
fun Reciepick() {
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
            composable("typeDetail/{typename}") { backStackEntry ->
                val typeName = backStackEntry.arguments?.getString("typeName") ?: ""
                com.mincorn.capstone.main.TypeDetail(typeName)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", R.drawable.chili, "홈"),
        NavigationItem("search", R.drawable.galic_icon, "검색"),
        NavigationItem("storage", R.drawable.greenonion_icon, "저장"),
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    val types = listOf(
        Type(R.drawable.logo, "정육/계란"),
        Type(R.drawable.logo, "채소"),
        Type(R.drawable.logo, "과일"),
        Type(R.drawable.logo, "수산"),
        Type(R.drawable.logo, "간편식품"),
        Type(R.drawable.logo, "조미료"),
        Type(R.drawable.logo, "베이커리"),
        Type(R.drawable.logo, "유제품"),
        Type(R.drawable.logo, "기타"),
    )

    val context = LocalContext.current

    Surface(
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = "홈",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "add",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 13.dp)
                        .size(24.dp)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp),
                content = {
                    items(types) { type ->
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(context, TypeDetailScreen::class.java)
                                    intent.putExtra("typeName", type.name)
                                    context.startActivity(intent)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(type.image),
                                contentDescription = "타입 아이콘",
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = type.name,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SearchScreen() {
    val recipes = listOf(
        Recipe("제육볶음", "간단한 최고의 반찬", R.drawable.vmon),
        Recipe("카레", "남은 음식 다 가져와", R.drawable.vmon),
        Recipe("라멘", "어후 맛있겠다", R.drawable.vmon),
    )

    val context = LocalContext.current

    Surface (
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = "레시피 검색",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(R.drawable.search),
                    contentDescription = "search",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 13.dp)
                        .size(24.dp)
                )
            }


            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )

            LazyColumn {
                items(recipes.size) { index ->
                    val recipe = recipes[index]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                val intent = Intent(context, PickRecipick::class.java)
                                intent.putExtra("pickName", recipe.name)
                                context.startActivity(intent)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = recipe.image),
                            contentDescription = recipe.name,
                            modifier = Modifier.size(64.dp),
                        )
                        Column(
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = recipe.name, fontWeight = FontWeight.Bold)
                            Text(text = recipe.description)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageScreen() {
    Surface (
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = "레시픽 저장소",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }


            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )
        }
    }
}

data class NavigationItem(val route: String, val icon: Int, val label: String)

data class Recipe(val name: String, val description: String, val image: Int)

data class Type(val image: Int, val name: String)