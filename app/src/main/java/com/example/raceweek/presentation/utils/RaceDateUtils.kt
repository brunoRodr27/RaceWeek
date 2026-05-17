package com.example.raceweek.presentation.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

// ── Funções legadas (UTC puro) — mantidas para compatibilidade ─────────────

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

fun Long.toSessionTimeString(): String {
    val ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC)
    val day = ldt.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .trimEnd('.')
        .replaceFirstChar { it.uppercase() }
    return "$day · ${String.format("%02d:%02d", ldt.hour, ldt.minute)}"
}

// ── Funções timezone-aware ─────────────────────────────────────────────────
//
// Os timestamps no Firestore representam o horário LOCAL da corrida armazenado
// como se fosse UTC (ex.: 15:00 Mônaco = "15:00 UTC"). O campo `raceTimezone`
// (ex.: "Europe/Monaco") re-âncora esse horário no fuso correto antes de
// converter para o fuso do dispositivo.

fun Long.toDeviceTimeString(raceTimezone: String): String {
    // O timestamp foi cadastrado tratando o horário LOCAL da corrida como fuso do
    // dispositivo de quem cadastrou (mesmo fuso do app). Primeiro "desfaz" esse fuso,
    // depois reaplica o fuso correto da corrida e converte para o dispositivo.
    val raceLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val deviceTime = raceLocal
        .atZone(safeZoneId(raceTimezone))
        .withZoneSameInstant(ZoneId.systemDefault())
    return String.format("%02d:%02d", deviceTime.hour, deviceTime.minute)
}

fun Long.toDeviceSessionTimeString(raceTimezone: String): String {
    val raceLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val deviceTime = raceLocal
        .atZone(safeZoneId(raceTimezone))
        .withZoneSameInstant(ZoneId.systemDefault())
    val day = deviceTime.dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
        .trimEnd('.')
        .replaceFirstChar { it.uppercase() }
    return "$day · ${String.format("%02d:%02d", deviceTime.hour, deviceTime.minute)}"
}

// Converte o timestamp armazenado (horário local da corrida como se fosse UTC-3) para
// o epoch real em UTC, usando o timezone correto da corrida. Usado no countdown do HeroCard.
fun Long.toCorrectEpochMillis(raceTimezone: String): Long {
    val raceLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    return raceLocal
        .atZone(safeZoneId(raceTimezone))
        .toInstant()
        .toEpochMilli()
}

// Retorna a abreviação do fuso do dispositivo (ex.: "BRT", "CEST", "PDT")
fun deviceTzAbbr(): String =
    ZonedDateTime.now()
        .zone
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())

// Converte o ID de timezone sem lançar exceção se o valor vier inválido do Firestore
private fun safeZoneId(tz: String): ZoneId = runCatching { ZoneId.of(tz) }.getOrDefault(ZoneOffset.UTC)
