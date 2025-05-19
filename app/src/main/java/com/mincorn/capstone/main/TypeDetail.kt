package com.mincorn.capstone.main

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mincorn.capstone.R

class TypeDetail : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val typeName = intent.getStringExtra("typeName") ?: ""
            val imageUri = intent.getStringExtra("imageUri") ?: ""
            val name = intent.getStringExtra("name") ?: ""
            val count = intent.getStringExtra("count") ?: ""
            TypeDetailScreen(typeName = typeName, imageUri = imageUri, name = name, count = count)
        }
    }
}

@Composable
fun TypeDetailScreen(typeName: String, imageUri: String, name: String, count: String) {
    val context = LocalContext.current
    val imageBitmap = remember(imageUri) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))

            BitmapFactory.decodeStream(inputStream)
        }.getOrNull()
    }

    var nameState by remember { mutableStateOf(name) }
    var countState by remember { mutableStateOf(count) }

    Surface (
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
                Text(
                    text = typeName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon (
                    painter = painterResource(R.drawable.add),
                    contentDescription = "add",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 13.dp)
                        .size(24.dp)
                        .clickable {  },
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            Text(
                text = nameState
            )

            Text(
                text = countState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TypeDetailPreview() {
    TypeDetailScreen(
        typeName = "정육/계란",
        imageUri = "",
        name = "",
        count = ""
    )
}