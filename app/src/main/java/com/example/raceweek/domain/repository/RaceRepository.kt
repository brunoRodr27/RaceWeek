package com.example.raceweek.domain.repository

import com.example.raceweek.domain.model.UpcomingRace

interface RaceRepository {
    suspend fun getUpcomingRaces(): List<UpcomingRace>
    suspend fun getAllRaces(): List<UpcomingRace>
}
