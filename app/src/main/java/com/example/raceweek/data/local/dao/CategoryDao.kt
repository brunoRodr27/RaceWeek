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

    @Query("SELECT * FROM Categories WHERE active = 1 ORDER BY [order] ASC")
    fun getActive(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM Categories WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CategoryEntity?

    @Query("UPDATE Categories SET active = :active WHERE id = :id")
    suspend fun updateActive(id: Int, active: Boolean)

    @Query("UPDATE Categories SET [order] = :order WHERE id = :id")
    suspend fun updateOrder(id: Int, order: Int)

    @Query("SELECT COALESCE(MAX([order]), -1) FROM Categories")
    suspend fun getMaxOrder(): Int
}
