package com.mincorn.capstone

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(this)
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}

object RetrofitInstance {
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.mincorn.co.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(): Retrofit {
        return retrofit
    }
}