package com.example.raceweek.domain.model

data class CalendarEvent(
    val flagResName: String,
    val name: String,
    val time: String,
    val series: String,
    val sessionLabel: String,
    val timestampMillis: Long
)
