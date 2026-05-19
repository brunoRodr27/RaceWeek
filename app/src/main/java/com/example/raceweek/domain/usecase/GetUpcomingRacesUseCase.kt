package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.repository.RaceRepository
import javax.inject.Inject

class GetUpcomingRacesUseCase @Inject constructor(
    private val raceRepository: RaceRepository
) {
    suspend operator fun invoke(): List<UpcomingRace> = raceRepository.getUpcomingRaces()
}
