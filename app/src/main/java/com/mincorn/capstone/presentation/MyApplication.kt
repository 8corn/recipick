package com.mincorn.capstone.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.tflite.java.TfLite
import com.kakao.sdk.common.KakaoSdk
import com.mincorn.capstone.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}