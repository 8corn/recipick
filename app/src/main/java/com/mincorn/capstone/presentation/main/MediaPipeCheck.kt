package com.mincorn.capstone.presentation.main

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

@Composable
fun MediaPipeCheckScreen(imageUri: String, name: String, count: String) {
    val context = LocalContext.current
    val imageBitmap = remember(imageUri) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))

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
                label = { Text("이름") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = countState,
                onValueChange = { countState = it },
                label = { Text("갯수") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    val intent = Intent(context, TypeDetail::class.java)
                        .apply {
                            putExtra("typeName", "정육/계란")
                            putExtra("imageUri", imageUri)
                            putExtra("name", nameState)
                            putExtra("count", countState)
                        }
                    context.startActivity(intent)
                }
            ) {
                Text(
                    text = "저장하기"
                )
            }
        }
    }
}