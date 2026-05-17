package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.repository.CategoryRepository
import javax.inject.Inject

class ReorderCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(orderedIds: List<Int>) =
        repository.reorderCategories(orderedIds)
}
