package com.example.raceweek.di

import android.content.Context
import androidx.room.Room
import com.example.raceweek.data.local.RaceWeekDatabase
import com.example.raceweek.data.local.dao.CategoryDao
import com.example.raceweek.data.local.dao.SettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RaceWeekDatabase =
        Room.databaseBuilder(
            context,
            RaceWeekDatabase::class.java,
            "raceweek.db"
        )
            .addMigrations(
                RaceWeekDatabase.MIGRATION_1_2,
                RaceWeekDatabase.MIGRATION_2_3
            )
            .build()

    @Provides
    fun provideCategoryDao(db: RaceWeekDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideSettingsDao(db: RaceWeekDatabase): SettingsDao = db.settingsDao()
}
