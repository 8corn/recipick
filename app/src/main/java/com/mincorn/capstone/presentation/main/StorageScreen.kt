package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mincorn.capstone.presentation.viewmodel.StorageViewModel

@Composable
fun StorageScreen(
    navController: NavController,
    storageViewModel: StorageViewModel = hiltViewModel()
) {
    val savedRecipe = storageViewModel.savedRecipes

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
                items(
                    count = savedRecipe.size,
                    key =  {index -> savedRecipe[index].name }
                ){index ->
                    val recipe = savedRecipe[index]

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                storageViewModel.deleteRecipe(recipe)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val isSwipingColor = if (dismissState.targetValue == dismissState.currentValue) Color.Red else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(isSwipingColor)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .fillMaxHeight()
                                        .background(Color.Red),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "삭제",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                    )
                                }
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
                                    Text(
                                        text = recipe.name,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "재료: ${recipe.ingredients}"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}