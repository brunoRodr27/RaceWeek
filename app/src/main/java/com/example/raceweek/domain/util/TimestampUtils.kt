package com.example.raceweek.domain.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Timestamps do Firestore representam o horário LOCAL da corrida armazenado como se
 * fosse o fuso do dispositivo de quem cadastrou. Esta função "re-ancora" o valor:
 * desfaz o fuso do dispositivo e reaplica o fuso real da corrida, retornando
 * o epoch UTC correto. Utilizada em use cases e na camada de apresentação.
 */
fun Long.reanchorToRaceTimezone(raceTimezone: String): Long {
    val raceLocal = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    return raceLocal.atZone(safeZoneId(raceTimezone)).toInstant().toEpochMilli()
}

fun safeZoneId(tz: String): ZoneId = runCatching { ZoneId.of(tz) }.getOrDefault(ZoneOffset.UTC)
