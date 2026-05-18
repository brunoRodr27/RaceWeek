package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.WeatherRemoteDataSource
import com.example.raceweek.domain.model.UpcomingRace
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetRaceWeatherUseCase @Inject constructor(
    private val dataSource: WeatherRemoteDataSource
) {
    suspend operator fun invoke(race: UpcomingRace): String? {
        val lat = race.lat ?: return null
        val lon = race.lon ?: return null

        val zone = runCatching { ZoneId.of(race.timezone) }.getOrDefault(ZoneOffset.UTC)

        // Re-ancora o timestamp armazenado (inserido no fuso do dispositivo) para o
        // fuso real da corrida — mesmo padrão usado no HeroCard/GetNextRaceUseCase.
        val localAtSystem = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(race.raceTimestamp), ZoneId.systemDefault()
        )
        val correctEpoch = localAtSystem.atZone(zone).toInstant().toEpochMilli()
        val raceLocalDt = Instant.ofEpochMilli(correctEpoch).atZone(zone).toLocalDateTime()

        val date = raceLocalDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        // A API retorna tempos no fuso passado via &timezone — busca pelo início da hora exata.
        val targetHour = raceLocalDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00"))

        return dataSource.fetchTemperatureAtRaceTime(lat, lon, date, targetHour, race.timezone)
            .getOrNull()
    }
}
