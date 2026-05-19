package com.example.raceweek.domain.usecase

import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.repository.CategoryRepository
import com.example.raceweek.domain.repository.RaceRepository
import com.example.raceweek.domain.util.reanchorToRaceTimezone
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetNextRaceUseCase @Inject constructor(
    private val raceRepository: RaceRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(preloadedRaces: List<UpcomingRace>? = null): HeroRaceInfo? {
        val now = System.currentTimeMillis()

        val activeDescriptions = categoryRepository.getActiveCategories()
            .first()
            .map { it.description }
            .toSet()

        if (activeDescriptions.isEmpty()) return null

        val races = preloadedRaces ?: raceRepository.getUpcomingRaces()

        return races
            .firstOrNull { race ->
                race.categoryDescription in activeDescriptions &&
                    race.raceTimestamp.reanchorToRaceTimezone(race.timezone) > now
            }
            ?.toHeroInfo()
    }
}

private fun UpcomingRace.toHeroInfo() = HeroRaceInfo(
    id = id,
    flagResName = flagResName,
    name = name,
    country = country,
    location = location,
    raceTimestamp = raceTimestamp,
    timezone = timezone
)
