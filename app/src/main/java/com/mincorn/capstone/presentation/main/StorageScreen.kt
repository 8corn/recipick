package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mincorn.capstone.presentation.viewmodel.StorageViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                    key =  {index -> "${savedRecipe[index].name}_$index" }
                ){index ->
                    val recipe = savedRecipe[index]

                    var showDialog by remember { mutableStateOf(false) }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text(
                                    text = "레시피 삭제",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = "'${recipe.name}'을(를) 저장소에서 삭제하시겠습니까?",
                                    fontSize = 14.sp,
                                    color = Color(0xFF868686),
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    modifier = Modifier
                                        .background(
                                            color = Color.Red,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    onClick = {
                                        storageViewModel.deleteRecipe(recipe)
                                        showDialog = false
                                    },
                                ) {
                                    Text(
                                        text = "삭제",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = Color.Black,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    onClick = {
                                        showDialog = false
                                    },
                                ) {
                                    Text(
                                        text = "취소",
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                        )
                    }

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                showDialog = true
                                false
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val backgroundColor = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                Color.Red
                            } else {
                                Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(backgroundColor)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .fillMaxHeight()
                                        .background(Color.Red)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            storageViewModel.deleteRecipe(recipe)
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "삭제",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                        },
                        content = {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                color = Color.White
                            ) {
                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                val encodedUrl = URLEncoder.encode(recipe.image, StandardCharsets.UTF_8.toString())
                                                val encodedDesc = URLEncoder.encode(recipe.ingredients, StandardCharsets.UTF_8.toString())
                                                val encodedRecipe = URLEncoder.encode(recipe.recipe, StandardCharsets.UTF_8.toString())

                                                navController.navigate("StorageRecipick/${recipe.name}/$encodedUrl/$encodedDesc/$encodedRecipe")
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
                                            val decodedDescription = URLDecoder.decode(recipe.description, StandardCharsets.UTF_8.toString())


                                            Text(
                                                text = recipe.name,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = decodedDescription,
                                                fontSize = 14.sp,
                                                color = Color(0xFF868686),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 5.dp, horizontal = 30.dp),
                                        thickness = 1.dp,
                                        color = Color(0xFF868686)
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