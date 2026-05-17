package com.example.raceweek.domain.model

data class UpcomingRace(
    val id: String,
    val flagResName: String,
    val categoryDescription: String,
    val name: String,
    val country: String,
    val location: String,
    val raceTimestamp: Long,
    val timezone: String = "UTC",
    val laps: Int? = null,
    val sessions: List<RaceSession> = emptyList()
)
