package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.repository.RaceRepository
import javax.inject.Inject

class GetAllRacesUseCase @Inject constructor(
    private val raceRepository: RaceRepository
) {
    suspend operator fun invoke(): List<UpcomingRace> = raceRepository.getAllRaces()
}
