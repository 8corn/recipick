package com.mincorn.capstone.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TypeDetailScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val typeName = intent.getStringExtra("typeName") ?: ""
            TypeDetail(typeName)
        }
    }
}

@Composable
fun TypeDetail(typeName: String) {
    Surface (
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Text(
                    text = typeName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                thickness = 1.dp,
                color = Color(0xFF868686)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TypeDetailPreview() {
    TypeDetail(
        typeName = "정육/계란",
    )
}