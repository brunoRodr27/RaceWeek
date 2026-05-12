package com.example.raceweek.di

import android.content.Context
import androidx.room.Room
import com.example.raceweek.data.local.RaceWeekDatabase
import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.entity.CategoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RaceWeekDatabase {
        val db = Room.databaseBuilder(
            context,
            RaceWeekDatabase::class.java,
            "raceweek.db"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            seedCategories(db.categoryDao())
        }

        return db
    }

    @Provides
    fun provideCategoryDao(db: RaceWeekDatabase): CategoryDao = db.categoryDao()

    private suspend fun seedCategories(dao: CategoryDao) {
        val existing = dao.getAll().first()
        if (existing.isEmpty()) {
            dao.insertAll(
                listOf(
                    CategoryEntity(name = "Formula 1", status = "T"),
                    CategoryEntity(name = "MotoGP", status = "T"),
                    CategoryEntity(name = "IndyCar", status = "T"),
                    CategoryEntity(name = "Formula E", status = "T"),
                    CategoryEntity(name = "WEC", status = "T"),
                    CategoryEntity(name = "NASCAR", status = "F"),
                )
            )
        }
    }
}
