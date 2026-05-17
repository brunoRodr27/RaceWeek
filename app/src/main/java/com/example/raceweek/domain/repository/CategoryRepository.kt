package com.example.raceweek.domain.repository

import com.example.raceweek.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getActiveCategories(): Flow<List<Category>>
    fun getAllCategories(): Flow<List<Category>>
    suspend fun syncCategories()
    suspend fun updateCategoryStatus(id: Int, active: Boolean)
    suspend fun reorderCategories(orderedIds: List<Int>)
}
