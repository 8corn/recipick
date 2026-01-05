package com.mincorn.capstone.presentation.main

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current

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
                                model = recipe.imageUrl,
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