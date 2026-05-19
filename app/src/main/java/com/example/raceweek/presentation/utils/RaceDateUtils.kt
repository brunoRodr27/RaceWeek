package com.example.raceweek.presentation.utils

import com.example.raceweek.domain.util.reanchorToRaceTimezone
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
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

// ── Funções timezone-aware ─────────────────────────────────────────────────
//
// Os timestamps no Firestore representam o horário LOCAL da corrida armazenado
// como se fosse UTC (ex.: 15:00 Mônaco = "15:00 UTC"). O campo `raceTimezone`
// (ex.: "Europe/Monaco") re-âncora esse horário no fuso correto antes de
// converter para o fuso do dispositivo.

fun Long.toDeviceTimeString(raceTimezone: String): String {
    val deviceTime = Instant.ofEpochMilli(this.reanchorToRaceTimezone(raceTimezone))
        .atZone(ZoneId.systemDefault())
    return String.format("%02d:%02d", deviceTime.hour, deviceTime.minute)
}

fun Long.toDeviceSessionTimeString(raceTimezone: String): String {
    val deviceTime = Instant.ofEpochMilli(this.reanchorToRaceTimezone(raceTimezone))
        .atZone(ZoneId.systemDefault())
    val day = deviceTime.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .trimEnd('.')
        .replaceFirstChar { it.uppercase() }
    return "$day · ${String.format("%02d:%02d", deviceTime.hour, deviceTime.minute)}"
}

fun deviceTzAbbr(): String =
    ZonedDateTime.now()
        .zone
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
