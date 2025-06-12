package com.mincorn.capstone

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mincorn.capstone.main.Gemini
import com.mincorn.capstone.main.TypeDetailScreen
import com.mincorn.capstone.main.TypeDetail
import com.mincorn.capstone.other.getFileFromUri
import com.mincorn.capstone.other.uploadImageToServer
import com.mincorn.capstone.recipick.PickRecipick
import com.mincorn.capstone.recipick.SearchRecipick
import com.mincorn.capstone.recipick.loadSavedRecipeFromFirebase
import com.mincorn.capstone.viewmodel.SearchViewModel
import com.mincorn.capstone.viewmodel.StorageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Reciepick()
        }
    }
}

@Composable
fun Reciepick() {
    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Gemini.init(context)
    }

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

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    val types = listOf(
        Type(R.drawable.meat, "정육/계란"),
        Type(R.drawable.carrot, "채소"),
        Type(R.drawable.fruit, "과일"),
        Type(R.drawable.seafood, "수산"),
        Type(R.drawable.burger, "간편식품"),
        Type(R.drawable.sauce, "조미료"),
        Type(R.drawable.bread, "베이커리"),
        Type(R.drawable.milk, "유제품"),
        Type(R.drawable.etc, "기타"),
    )

    val context = LocalContext.current

    val photoUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { File(context.cacheDir, "capture_image.jpg") }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri.value != null) {
            val file = getFileFromUri(context, photoUri.value!!) ?: return@rememberLauncherForActivityResult

            CoroutineScope(Dispatchers.IO).launch {
                val response = uploadImageToServer(file, context)
                Log.d("MediaPipe", response)

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
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
                        .clickable {
                            photoUri.value = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                imageFile
                            )
                            photoUri.value?.let { takePictureLauncher.launch(it) }
                        }
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
                    .padding(vertical = 150.dp),
                content = {
                    items(types) { type ->
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .padding(bottom = 14.dp)
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(context, TypeDetail::class.java)
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

                            Spacer(modifier = Modifier.height(7.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    val context = LocalContext.current

    val viewModel: SearchViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    )

    val recipes = viewModel.recipes
    val isRefreshing = viewModel.isRefreshing

    val pullToRefreshState = rememberPullToRefreshState()

    Surface (
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.loadRecipes()
                }
            ),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
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
                        .clickable {
                            val intent = Intent(context, SearchRecipick::class.java)
                            context.startActivity(intent)
                        },
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.loadRecipes()
                },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn (
                    modifier = Modifier.fillMaxSize()
                ) {
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
                            AsyncImage(
                                model = recipe.image,
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
}

@Composable
fun StorageScreen() {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val viewModel: StorageViewModel = viewModel()

    LaunchedEffect(uid) {
        if (uid != null) {
            loadSavedRecipeFromFirebase(uid) { recipes ->
                RecipeStorage.savedRecipes.clear()
                RecipeStorage.savedRecipes.addAll(recipes)
            }
        }
    }

    val savedRecipe = viewModel.savedRecipes

    Surface (
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
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

            LazyColumn {
                items(savedRecipe.size, key =  {index -> savedRecipe[index].name }){index ->
                    val recipe = savedRecipe[index]

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                val uid2 = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid2 != null) {
                                    deleteRecipeFromFirebase(uid2, recipe)
                                }
                                savedRecipe.remove(recipe)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = "삭제",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .padding(end = 24.dp)
                                        .background(Color.Red)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        content = {
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                AsyncImage(
                                    model = recipe.image,
                                    contentDescription = recipe.name,
                                    modifier = Modifier.size(64.dp),
                                )

                                Column (
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(text = recipe.name, fontWeight = FontWeight.Bold)
                                    Text(text = "재료: ${recipe.ingredients}")
                                    Text(text = "레시피: ${recipe.recipe}")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

object RecipeStorage {
    val savedRecipes = mutableStateListOf<SavedRecipe>()

    fun add(recipe: SavedRecipe) {
        savedRecipes.add(recipe)
    }
}

fun deleteRecipeFromFirebase(uid: String, recipe: SavedRecipe) {
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("user").document(uid).collection("storage")

    collection
        .whereEqualTo("name", recipe.name)
        .whereEqualTo("ingredients", recipe.ingredients)
        .whereEqualTo("recipe", recipe.recipe)
        .get()
        .addOnSuccessListener { result ->
            for (document in result.documents) {
                document.reference.delete()
            }
        }
        .addOnFailureListener {
            Log.e("Firebase", "레시피 삭제 실패", it)
        }
}

data class NavigationItem(val route: String, val icon: Int, val label: String)

data class Recipe(val name: String, val description: String, val image: String)

data class Type(val image: Int, val name: String)

data class SavedRecipe (val name: String = "", val ingredients: String = "", val recipe: String = "", val image: String = "")