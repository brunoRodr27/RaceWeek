package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.repository.CategoryRepository
import javax.inject.Inject

class SyncCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke() = repository.syncCategories()
}
