package com.example.raceweek.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.raceweek.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories")
    fun getAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM Categories WHERE status = 'T'")
    fun getActive(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("DELETE FROM Categories")
    suspend fun deleteAll()
}
