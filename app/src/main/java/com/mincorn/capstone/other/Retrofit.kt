package com.mincorn.capstone.other

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class UnsplashResponse(val results: List<UnsplashPhoto>)
data class UnsplashPhoto(val urls: Urls)
data class Urls(val small: String, val full: String)

interface UnsplashApi {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("client_id") clientId: String
    ): UnsplashResponse
}

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: UnsplashApi = retrofit.create(UnsplashApi::class.java)
}