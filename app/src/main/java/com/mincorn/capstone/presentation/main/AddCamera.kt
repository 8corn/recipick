package com.mincorn.capstone.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mincorn.capstone.R
import com.mincorn.capstone.presentation.viewmodel.DetectionViewModel
import java.io.File

@Composable
fun AddCamera (
    navController: NavController,
    detectionViewModel: DetectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val apiKey = stringResource(R.string.gemini)

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Log.e("Camera", "카메라 권한 없음")
            Toast.makeText(context, "카메라 권한 없음\n설정 -> 애플리케이션 -> 레시픽 -> 권한에서 허용해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }

        detectionViewModel.prepareAi()
    }

    LaunchedEffect(lensFacing, hasCameraPermission) {
        if (!hasCameraPermission) return@LaunchedEffect

        val cameraProviders = ProcessCameraProvider.getInstance(context)

        cameraProviders.addListener({
            try {
                val cameraProvider = cameraProviders.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val capture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
                imageCapture = capture

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.Builder().requireLensFacing(lensFacing).build(),
                    preview,
                    capture
                )
                Log.d("AddCamera", "카메라 바인딩 성공: $lensFacing")
            } catch (e: Exception) {
                Log.e("AddCamera", "카메라 바인딩 실패", e)
                Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))
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
                            .clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.popBackStack()
                            }
                    )

                    Text(
                        text = "사진",
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
                            .clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                Toast.makeText(context, "이미지 분석 중입니다.", Toast.LENGTH_LONG).show()
                                Log.d("AddCamera", "이미지 분석 중입니다.")

                                imageCapture?.let { capture ->
                                    val photoFile = File(
                                        context.cacheDir,
                                        "photo_${System.currentTimeMillis()}.jpg"
                                    )
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                    capture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                Log.d("Camera", "사진 찍음: ${photoFile.absolutePath}")

                                                detectionViewModel.analyzeWithGemini(
                                                    apiKey = apiKey,
                                                    photoFile = photoFile
                                                ) { route ->
                                                    navController.navigate(route) {
                                                        popUpTo("AddCamera") {
                                                            inclusive = true
                                                        }
                                                    }
                                                }

//                                                val route = detectionViewModel.processImageAndGetRoute(photoFile)
//
//                                                ContextCompat.getMainExecutor(context).execute {
//                                                    if (route.isEmpty()) {
//                                                        Toast.makeText(context, "식재료를 인식하지 못했습니다. 다시 찍어주세요.",Toast.LENGTH_LONG).show()
//
//                                                        return@execute
//                                                    }
//                                                    try {
//                                                        navController.navigate(route) {
//                                                            popUpTo("AddCamera") {
//                                                                inclusive = true
//                                                            }
//                                                        }
//                                                    } catch (e: Exception) {
//                                                        Log.e("Navigation", "경로 이동 실패: $route", e)
//                                                        Toast.makeText(context, "분석 결과를 표시할 수 없습니다.\n다시 시도해주세요.",Toast.LENGTH_SHORT).show()
//                                                    }
//                                                }
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("Camera", "사진 전송 실패", exception)
                                                Toast.makeText(context, "사진 촬영에 실패하였습니다.",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
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
                            .clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                lensFacing =
                                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                        Log.d("Camera", "카메라 뒤로 회전")
                                        CameraSelector.LENS_FACING_BACK
                                    }
                                    else {
                                        Log.d("Camera", "카메라 앞으로 회전")
                                        CameraSelector.LENS_FACING_FRONT
                                    }
                            }
                    )
                }
            }
        }
    }
}

//앱의 설정 화면 바로 열어주는 함수
//fun openAppSettings(context: Context) {
//    val intent = Intent(
//        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//        Uri.fromParts("package", context.packageName, null)
//    )
//    context.startActivity(intent)
//}