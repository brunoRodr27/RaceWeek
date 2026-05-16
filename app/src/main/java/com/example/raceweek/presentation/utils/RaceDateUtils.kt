package com.example.raceweek.presentation.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

fun Long.toRaceDateString(): String {
    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC)
    val day = ldt.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .trimEnd('.')
        .replaceFirstChar { it.uppercase() }
    val month = ldt.month
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .trimEnd('.')
        .replaceFirstChar { it.uppercase() }
    return "$day, ${ldt.dayOfMonth} $month"
}

fun Long.toRaceTimeString(): String {
    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC)
    return String.format("%02d:%02d", ldt.hour, ldt.minute)
}
