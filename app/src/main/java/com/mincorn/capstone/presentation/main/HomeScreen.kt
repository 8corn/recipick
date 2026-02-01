package com.mincorn.capstone.presentation.main

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.uiModel.Type
import com.mincorn.capstone.presentation.viewmodel.DetectionViewModel
import java.net.URLEncoder

@Composable
fun HomeScreen(
    navController: NavController,
    detectionViewModel: DetectionViewModel,
    onCameraClick: () -> Unit
) {
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
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onCameraClick()
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
                                .clickable (
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    val encodedTypeName = URLEncoder.encode(type.name, "UTF-8").replace("+", "%20")
                                    navController.navigate("typeDetail/$encodedTypeName")
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = type.image,
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