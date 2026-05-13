package com.example.raceweek.domain.repository

import com.example.raceweek.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    fun getActiveCategories(): Flow<List<Category>>
    suspend fun syncCategories()
}
