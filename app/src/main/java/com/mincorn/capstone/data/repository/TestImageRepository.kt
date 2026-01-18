package com.mincorn.capstone.data.repository

import com.mincorn.capstone.domain.respository.ImageRepository
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

class FakeImageRepository @Inject constructor() : ImageRepository {
    override suspend fun uploadImage(imageFile: File): String {
        delay(500)

        return "양파"
    }
}