package com.mincorn.capstone.data.di

import com.mincorn.capstone.data.repository.FakeImageRepository
import com.mincorn.capstone.data.repository.ImageRepositoryImpl
import com.mincorn.capstone.data.repository.RecipeRepositoryImpl
import com.mincorn.capstone.data.repository.StorageRepositoryImpl
import com.mincorn.capstone.domain.respository.ImageRepository
import com.mincorn.capstone.domain.respository.RecipeRepository
import com.mincorn.capstone.domain.respository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        storageRepositoryImpl: StorageRepositoryImpl
    ): StorageRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository

//    @Binds
//    @Singleton
//    abstract fun bindImageRepository(
//        imageRepositoryImpl: ImageRepositoryImpl
//    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindFakeImageRepository(
        imageRepository: FakeImageRepository
    ): ImageRepository
}