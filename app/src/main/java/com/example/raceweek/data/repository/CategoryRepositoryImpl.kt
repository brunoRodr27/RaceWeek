package com.example.raceweek.data.repository

import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.entity.CategoryEntity
import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.domain.model.Category
import com.example.raceweek.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao,
    private val remoteDataSource: FirestoreRemoteDataSource
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getActiveCategories(): Flow<List<Category>> =
        dao.getActive().map { list -> list.map { it.toDomain() } }

    override suspend fun syncCategories() {
        remoteDataSource.fetchCategories().onSuccess { remoteList ->
            remoteList.forEach { remote ->
                if (dao.getByName(remote.name) == null) {
                    dao.insertAll(
                        listOf(
                            CategoryEntity(
                                name = remote.name,
                                status = "T",
                                description = remote.description
                            )
                        )
                    )
                }
            }
        }
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        active = status == "T",
        description = description
    )
}
