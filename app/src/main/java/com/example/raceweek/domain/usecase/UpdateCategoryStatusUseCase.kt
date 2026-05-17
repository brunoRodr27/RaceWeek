package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.repository.CategoryRepository
import javax.inject.Inject

class UpdateCategoryStatusUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(id: Int, active: Boolean) =
        repository.updateCategoryStatus(id, active)
}
