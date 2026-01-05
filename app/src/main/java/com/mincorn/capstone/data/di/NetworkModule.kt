package com.mincorn.capstone.data.di

import android.content.Context
import com.mincorn.capstone.R
import com.mincorn.capstone.data.repository.RecipeRepositoryImpl
import com.mincorn.capstone.data.source.remote.Gemini
import com.mincorn.capstone.data.source.remote.UnsplashApi
import com.mincorn.capstone.domain.model.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi {
        return retrofit.create(UnsplashApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGemini(@ApplicationContext context: Context): Gemini {
        val apiKey = context.getString(R.string.gemini)
        return Gemini(apiKey)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        geminiDataSource: Gemini,
        api: UnsplashApi,
        @ApplicationContext context: Context
    ): RecipeRepository {
        val accessKey = context.getString(R.string.unsplash_access_key)
        return RecipeRepositoryImpl(geminiDataSource, api, accessKey)
    }
}