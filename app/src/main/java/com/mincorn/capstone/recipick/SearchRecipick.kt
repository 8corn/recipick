package com.mincorn.capstone.recipick

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mincorn.capstone.R
import com.mincorn.capstone.recipick.ui.theme.CapstoneTheme

class SearchRecipick : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchRecipickScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchRecipickScreen() {
    val (query, setQuery) = remember { mutableStateOf("") }

    Surface (
        color = Color.White
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 38.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = setQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 43.dp, vertical = 16.dp)
                        .border(1.dp, Color(0xFF868686), RoundedCornerShape(40.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        cursorColor = Color(0xFF868686),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "원하는 레시피를 검색해주세요.",
                            color = Color(0xFF868686),
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 247.dp)
                                .size(24.dp),
                            tint = Color(0xFF868686)
                        )
                    }
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 90.dp)
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0xFF868686),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding()
                        .verticalScroll(rememberScrollState())
                ) {
//                    LazyColumn {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                                .clickable {},
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.Center,
//                        ) {
//                            Image(
//                                painter = painterResource(R.drawable.vmon),
//                                contentDescription = "recipe.name",
//                                modifier = Modifier.size(64.dp),
//                            )
//                            Column(
//                                modifier = Modifier.padding(start = 8.dp)
//                            ) {
//                                Text(text = "recipe.name", fontWeight = FontWeight.Bold)
//                                Text(text = "recipe.description")
//                            }
//                        }
//                    }
                }
            }
        }
    }
}