package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.model.Category
import com.example.raceweek.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.getActiveCategories()
}
