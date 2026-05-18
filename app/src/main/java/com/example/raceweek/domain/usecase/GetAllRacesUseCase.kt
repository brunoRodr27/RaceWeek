package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.domain.model.RaceSession
import com.example.raceweek.domain.model.UpcomingRace
import javax.inject.Inject

class GetAllRacesUseCase @Inject constructor(
    private val remoteDataSource: FirestoreRemoteDataSource
) {
    suspend operator fun invoke(): List<UpcomingRace> =
        remoteDataSource.fetchAllRaces()
            .getOrElse { emptyList() }
            .map { remote ->
                UpcomingRace(
                    id = remote.id,
                    flagResName = remote.flagResName,
                    categoryDescription = remote.categoryDescription,
                    name = remote.name,
                    country = remote.country,
                    location = remote.location,
                    raceTimestamp = remote.raceTimestampMillis,
                    timezone = remote.timezone,
                    laps = remote.laps,
                    lat = remote.lat,
                    lon = remote.lon,
                    sessions = remote.sessions.map { s ->
                        RaceSession(key = s.key, timestamp = s.timestampMillis)
                    }
                )
            }
}
