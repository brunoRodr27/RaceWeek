package com.example.raceweek.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.entity.CategoryEntity

@Database(
    entities = [CategoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RaceWeekDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Categories ADD COLUMN description TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
