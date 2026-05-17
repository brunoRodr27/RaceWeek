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

    // Só insere categorias genuinamente novas; preserva status e order das existentes.
    // Novas categorias recebem order = MAX(order) + 1, garantindo unicidade.
    override suspend fun syncCategories() {
        remoteDataSource.fetchCategories().onSuccess { remoteList ->
            remoteList.forEach { remote ->
                if (dao.getByName(remote.name) == null) {
                    val nextOrder = dao.getMaxOrder() + 1
                    dao.insertAll(
                        listOf(
                            CategoryEntity(
                                name = remote.name,
                                status = "T",
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
        dao.updateStatus(id, if (active) "T" else "F")
    }

    // Recebe os IDs na nova sequência desejada e atribui order 0, 1, 2...
    override suspend fun reorderCategories(orderedIds: List<Int>) {
        orderedIds.forEachIndexed { index, id ->
            dao.updateOrder(id, index)
        }
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        active = status == "T",
        description = description,
        order = order
    )
}
