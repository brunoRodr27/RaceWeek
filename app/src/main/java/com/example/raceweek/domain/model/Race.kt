package com.example.raceweek.domain.model

data class Race(
    val id: String = "",
    val category: String,
    val flag: String,
    val name: String,
    val location: String,
    val date: String,
    val time: String,
    val weatherIcon: String,
    val temperature: String,
    val isHero: Boolean = false
)

data class CalendarEvent(
    val flagResName: String,
    val name: String,
    val time: String,
    val series: String,
    val sessionLabel: String,
    val timestampMillis: Long
)
