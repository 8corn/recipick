package com.mincorn.capstone.other

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mincorn.capstone.MainActivity
import com.mincorn.capstone.R
import java.io.File

class AddCamera : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AddCameraScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCameraScreen() {
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val capture = ImageCapture.Builder().build()
        imageCapture = capture

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.Builder().requireLensFacing(lensFacing).build(),
                preview,
                capture
            )
        } catch (e: Exception) {
            Log.e("Camera", "카메라 바인딩 실패", e)
        }
    }

    Surface(
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 34.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Image(
                        painter = painterResource(R.drawable.back_arrow),
                        contentDescription = "back_arrow",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(top = 2.dp)
                            .clickable {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                    )

                    Text(
                        text = "실시간 촬영",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Surface (
                color = Color.White,
                modifier = Modifier
                    .height(198.dp)
            ) {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.camera_click),
                        contentDescription = "camera_click",
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                            .clickable {
                                imageCapture?.let { capture ->
                                    val photoFile = File(
                                        context.cacheDir,
                                        "photo_${System.currentTimeMillis()}.jpg"
                                    )
                                    val outputOptions =
                                        ImageCapture.OutputFileOptions.Builder(photoFile)
                                            .build()
                                    capture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                Log.d(
                                                    "Camera",
                                                    "사진 찍음: ${photoFile.absolutePath}"
                                                )
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("Camera", "사진 전송 실패", exception)
                                            }
                                        }
                                    )
                                }
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            },
                    )

                    Spacer(modifier = Modifier.width(81.dp))

                    Image(
                        painter = painterResource(R.drawable.camera_turn),
                        contentDescription = "camera_turn",
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = (-28).dp)
                            .clickable {
                                lensFacing =
                                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                        CameraSelector.LENS_FACING_BACK
                                        Log.d("Camera", "카메라 뒤로 회전")
                                    }
                                    else {
                                        CameraSelector.LENS_FACING_FRONT
                                        Log.d("Camera", "카메라 앞으로 회전")
                                    }
                            }
                    )
                }
            }
        }
    }
}