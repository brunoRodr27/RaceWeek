package com.example.raceweek.data.repository

import com.example.raceweek.data.remote.FirestoreRemoteDataSource
import com.example.raceweek.data.remote.RemoteUpcomingRace
import com.example.raceweek.domain.model.RaceSession
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.repository.RaceRepository
import com.example.raceweek.domain.util.reanchorToRaceTimezone
import javax.inject.Inject

class RaceRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirestoreRemoteDataSource
) : RaceRepository {

    // O filtro de "futuras" é feito aqui, após reanchorar o timestamp pelo fuso real da corrida.
    // A query do Firestore não pode fazer esse filtro porque o timestamp armazenado representa
    // o horário local como se fosse o fuso do dispositivo de cadastro (UTC-3), não UTC real.
    override suspend fun getUpcomingRaces(): List<UpcomingRace> {
        val now = System.currentTimeMillis()
        return remoteDataSource.fetchAllRaces()
            .getOrElse { return emptyList() }
            .map { it.toDomain() }
            .filter { it.raceTimestamp.reanchorToRaceTimezone(it.timezone) >= now }
    }

    override suspend fun getAllRaces(): List<UpcomingRace> =
        remoteDataSource.fetchAllRaces().getOrElse { emptyList() }.map { it.toDomain() }
}

internal fun RemoteUpcomingRace.toDomain() = UpcomingRace(
    id = id,
    flagResName = flagResName,
    categoryDescription = categoryDescription,
    name = name,
    country = country,
    location = location,
    raceTimestamp = raceTimestampMillis,
    timezone = timezone,
    laps = laps,
    lat = lat,
    lon = lon,
    sessions = sessions.map { RaceSession(key = it.key, timestamp = it.timestampMillis) }
)
