package com.mincorn.capstone.presentation.main

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.viewmodel.DetectionViewModel
import com.mincorn.capstone.presentation.viewmodel.SearchViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = hiltViewModel(),
    detectionViewModel: DetectionViewModel = hiltViewModel(),
    onRecipeClick: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    val ingredients = detectionViewModel.fridgeIngredients
    val isInitialLoading = detectionViewModel.isInitialLoading

    val recipes = searchViewModel.recipes
    val isRefreshing = searchViewModel.isRefreshing

    LaunchedEffect(ingredients, isInitialLoading) {
        if (isInitialLoading) return@LaunchedEffect

        val ingredientNames = ingredients.map { it.name }

        if (ingredientNames.isNotEmpty()) {
            searchViewModel.loadRecipes(ingredientNames)
        } else {
            searchViewModel.recipes.clear()
            Toast.makeText(context, "현재 가지고 계신 재료가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Surface (
        modifier = Modifier
            .fillMaxSize(),
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
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate("SearchRecipick")
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
                    val ingredientNames = ingredients.map { it.name }

                    if (ingredientNames.isNotEmpty()) {
                        searchViewModel.loadRecipes(ingredientNames)
                    } else {
                        searchViewModel.recipes.clear()
                        Toast.makeText(context, "현재 가지고 계신 재료가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                },
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
                                .clickable (
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onRecipeClick(recipe.name, recipe.imageUrl, recipe.description)
                                },
                        ) {
                            AsyncImage(
                                model = recipe.imageUrl,
                                contentDescription = recipe.name,
                                modifier = Modifier
                                    .size(64.dp),
                            )
                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = recipe.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )

                                Text(
                                    text = recipe.description,
                                    color = Color(0xFF868686),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}