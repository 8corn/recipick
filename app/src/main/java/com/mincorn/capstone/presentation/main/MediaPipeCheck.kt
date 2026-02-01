package com.mincorn.capstone.presentation.main

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MediaPipeCheck(
    navController: NavController,
    imageUri: String,
    name: String,
    count: String
) {
    val context = LocalContext.current
    val imageBitmap = remember(imageUri) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(imageUri.toUri())

            BitmapFactory.decodeStream(inputStream)
        }.getOrNull()
    }

    var nameState by remember { mutableStateOf(name) }
    var countState by remember { mutableStateOf(count) }
    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(20.dp)
                )
            }
            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = {
                    Text("이름")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = countState,
                onValueChange = { countState = it },
                label = {
                    Text("갯수")
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    val encodedUri = URLEncoder.encode(imageUri, StandardCharsets.UTF_8.toString())

                    val typeName = "정육_계란"
                    navController.navigate("typeDetail/$typeName/$encodedUri/$nameState/$countState") {
                        popUpTo("MediaPipeConnect") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(
                    text = "저장하기"
                )
            }
        }
    }
}