package com.mincorn.capstone.presentation.recipick

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mincorn.capstone.R
import com.mincorn.capstone.domain.model.SavedRecipe
import com.mincorn.capstone.presentation.other.RecipeInstructions
import com.mincorn.capstone.presentation.viewmodel.SearchViewModel
import com.mincorn.capstone.presentation.viewmodel.StorageViewModel

@Composable
fun PickRecipick(
    navController: NavController,
    pickName: String,
    imageUrl: String,
    description: String,
    searchViewModel: SearchViewModel = hiltViewModel(),
    firebaseViewModel: StorageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val detail = searchViewModel.detail

    LaunchedEffect(pickName) {
        searchViewModel.loadDetailRecipe(pickName)
    }

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 38.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.back_arrow),
                    contentDescription = "back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 13.dp)
                        .size(24.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.popBackStack()
                        }
                )

                Text(
                    text = pickName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Icon(
                    painter = painterResource(R.drawable.save),
                    contentDescription = "save",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 13.dp)
                        .size(24.dp)
                        .clickable {
                            detail?.let {
                                val savedRecipe = SavedRecipe(
                                    name = pickName,
                                    description = description,
                                    ingredients = it.ingredients,
                                    recipe = it.instructions,
                                    image = imageUrl
                                )
                                firebaseViewModel.saveRecipe(savedRecipe)
                                Toast.makeText(context, "저장소에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686),
            )

            if (searchViewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "레시피를 생성 중입니다.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 16.dp)
                    )
                }
            } else if (detail != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "레시피 이미지",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                            .size(280.dp)
                    )

                    Text(
                        text = "필요한 재료",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = detail.ingredients,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "레시픽",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    RecipeInstructions(detail.instructions)

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}