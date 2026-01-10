package com.mincorn.capstone.data.source.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.classifier.ImageClassifier

class ImageAnalyzer (
    private val context: Context
) {
    private var imageClassifier: ImageClassifier? = null

    fun setupClassifier() {
        TfLite.initialize(context).addOnSuccessListener {
            try {
                val options = ImageClassifier.ImageClassifierOptions.builder()
                    .setBaseOptions(BaseOptions.builder().useGpu().build())
                    .setMaxResults(3)
                    .setScoreThreshold(0.5f)
                    .build()

                imageClassifier = ImageClassifier.createFromFileAndOptions(
                    context, "food_model.tflite", options
                )
                Log.d("TFLite", "모델 로드 성공!")
            } catch (e: Exception) {
                Log.e("TFLite", "모델 로드 실패: ${e.message}")
            }
        }
    }

    fun analyze(bitmap: Bitmap): String {
        val image = TensorImage.fromBitmap(bitmap)
        val results = imageClassifier?.classify(image)

        return results?.firstOrNull()?.categories?.firstOrNull()?.label ?: "Unknown"
    }
}