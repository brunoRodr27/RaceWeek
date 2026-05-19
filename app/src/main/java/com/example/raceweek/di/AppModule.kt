package com.example.raceweek.di

import com.example.raceweek.data.repository.CategoryRepositoryImpl
import com.example.raceweek.data.repository.RaceRepositoryImpl
import com.example.raceweek.domain.repository.CategoryRepository
import com.example.raceweek.domain.repository.RaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindRaceRepository(impl: RaceRepositoryImpl): RaceRepository
}
