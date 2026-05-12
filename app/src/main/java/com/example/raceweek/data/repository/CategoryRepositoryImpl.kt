package com.example.raceweek.data.repository

import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.entity.CategoryEntity
import com.example.raceweek.domain.model.Category
import com.example.raceweek.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getActiveCategories(): Flow<List<Category>> =
        dao.getActive().map { list -> list.map { it.toDomain() } }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        active = status == "T"
    )
}
