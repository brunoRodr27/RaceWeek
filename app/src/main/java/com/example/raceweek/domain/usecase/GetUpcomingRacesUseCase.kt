package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.domain.model.UpcomingRace
import javax.inject.Inject

class GetUpcomingRacesUseCase @Inject constructor(
    private val remoteDataSource: FirestoreRemoteDataSource
) {
    suspend operator fun invoke(): List<UpcomingRace> =
        remoteDataSource.fetchUpcomingRaces()
            .getOrElse { emptyList() }
            .map { remote ->
                UpcomingRace(
                    id = remote.id,
                    flagResName = remote.flagResName,
                    categoryDescription = remote.categoryDescription,
                    name = remote.name,
                    country = remote.country,
                    location = remote.location,
                    raceTimestamp = remote.raceTimestampMillis
                )
            }
}
