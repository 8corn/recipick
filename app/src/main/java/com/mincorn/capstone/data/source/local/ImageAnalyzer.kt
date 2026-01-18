package com.mincorn.capstone.data.source.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.android.gms.tflite.java.TfLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

// 카메라를 통해 들어오는 시각 정보를 AI 모델이 이해할 수 있는 데이터로 가공하고, 그 결과를 받아오는 역할
class ImageAnalyzer (
    private val context: Context
) {
    private var imageClassifier: ImageClassifier? = null
    @Volatile
    private var isInitialized = false

    fun setupClassifier() {
        if (isInitialized) return

        try {
            val options = ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(3)
                .setScoreThreshold(0.5f)
                .build()

            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context, "food_model.tflite", options
            )

            isInitialized = true
            Log.d("TFLite", "모델 로드 성공!")
        } catch (e: Exception) {
            Log.e("TFLite", "모델 로드 실패: ${e.message}")
            isInitialized = false
        }
    }

    fun analyze(bitmap: Bitmap): String {
        val classifier = imageClassifier

        if (!isInitialized || classifier == null) {
            return "준비중..."
        }

        return try {
            val resizedBitmap = bitmap.scale(224, 224)
            val image = TensorImage.fromBitmap(resizedBitmap)

            val results = classifier.classify(image)
            results?.firstOrNull()?.categories?.firstOrNull()?.label ?: "Unknown"
        } catch (e: Exception) {
            Log.e("TFLite", "추론 중 에러 발생: ${e.message}")
            "Error"
        }
    }
}