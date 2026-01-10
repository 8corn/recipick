package com.mincorn.capstone.data.source.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class Urls(
    val small: String,
    val full: String
)
data class UnsplashPhoto(
    val urls: Urls
)
data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)

interface UnsplashApi {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("client_id") clientId: String,
    ): UnsplashResponse
}