package com.mincorn.capstone.presentation

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.mincorn.capstone.R
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}