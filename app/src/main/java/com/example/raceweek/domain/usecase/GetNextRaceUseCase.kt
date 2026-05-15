package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.domain.model.HeroRaceInfo
import javax.inject.Inject

class GetNextRaceUseCase @Inject constructor(
    private val remoteDataSource: FirestoreRemoteDataSource
) {
    suspend operator fun invoke(): HeroRaceInfo? =
        remoteDataSource.fetchNextRace()
            .getOrNull()
            ?.let { remote ->
                HeroRaceInfo(
                    flagResName = remote.flagResName,
                    name = remote.name,
                    country = remote.country,
                    location = remote.location,
                    raceTimestamp = remote.raceTimestampMillis
                )
            }
}
