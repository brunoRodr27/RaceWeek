package com.example.raceweek.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.entity.CategoryEntity

@Database(
    entities = [CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RaceWeekDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}
