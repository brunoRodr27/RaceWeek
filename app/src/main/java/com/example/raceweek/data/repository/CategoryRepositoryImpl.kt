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

    override fun getActiveCategories(): Flow<List<Category>> =
        dao.getActive().map { list -> list.map { it.toDomain() } }

    override fun getAllCategories(): Flow<List<Category>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun syncCategories() {
        remoteDataSource.fetchCategories().onSuccess { remoteList ->
            remoteList.forEach { remote ->
                if (dao.getByName(remote.name) == null) {
                    val nextOrder = dao.getMaxOrder() + 1
                    dao.insertAll(
                        listOf(
                            CategoryEntity(
                                name = remote.name,
                                active = true,
                                description = remote.description,
                                order = nextOrder
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun updateCategoryStatus(id: Int, active: Boolean) {
        dao.updateActive(id, active)
    }

    override suspend fun reorderCategories(orderedIds: List<Int>) {
        orderedIds.forEachIndexed { index, id ->
            dao.updateOrder(id, index)
        }
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        active = active,
        description = description,
        order = order
    )
}
