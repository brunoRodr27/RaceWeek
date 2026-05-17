package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.domain.model.HeroRaceInfo
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class GetNextRaceUseCase @Inject constructor(
    private val remoteDataSource: FirestoreRemoteDataSource
) {
    suspend operator fun invoke(): HeroRaceInfo? {
        val now = System.currentTimeMillis()
        // fetchUpcomingRaces retorna os resultados já ordenados por data (orderBy "race").
        // Iteramos até o primeiro cuja hora real (timezone corrigido) ainda não passou.
        val races = remoteDataSource.fetchUpcomingRaces().getOrNull() ?: return null

        val next = races.firstOrNull { race ->
            reanchoredEpoch(race.raceTimestampMillis, race.timezone) > now
        } ?: return null

        return HeroRaceInfo(
            id = next.id,
            flagResName = next.flagResName,
            name = next.name,
            country = next.country,
            location = next.location,
            raceTimestamp = next.raceTimestampMillis,
            timezone = next.timezone
        )
    }

    private fun reanchoredEpoch(storedMillis: Long, raceTimezone: String): Long {
        val raceLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(storedMillis), ZoneId.systemDefault())
        val safeZone = runCatching { ZoneId.of(raceTimezone) }.getOrDefault(ZoneOffset.UTC)
        return raceLocal.atZone(safeZone).toInstant().toEpochMilli()
    }
}
