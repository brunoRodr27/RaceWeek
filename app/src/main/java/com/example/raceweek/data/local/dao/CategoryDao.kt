package com.example.raceweek.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.raceweek.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Categories ORDER BY [order] ASC")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM Categories WHERE status = 'T' ORDER BY [order] ASC")
    fun getActive(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM Categories WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CategoryEntity?

    @Query("DELETE FROM Categories")
    suspend fun deleteAll()

    @Query("UPDATE Categories SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE Categories SET [order] = :order WHERE id = :id")
    suspend fun updateOrder(id: Int, order: Int)

    // Retorna -1 quando a tabela está vazia; o próximo order será 0.
    @Query("SELECT COALESCE(MAX([order]), -1) FROM Categories")
    suspend fun getMaxOrder(): Int
}
