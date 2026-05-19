package com.example.raceweek.domain.usecase

import com.example.raceweek.data.remote.WeatherRemoteDataSource
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.util.reanchorToRaceTimezone
import com.example.raceweek.domain.util.safeZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetRaceWeatherUseCase @Inject constructor(
    private val dataSource: WeatherRemoteDataSource
) {
    suspend operator fun invoke(race: UpcomingRace): String? {
        val lat = race.lat ?: return null
        val lon = race.lon ?: return null

        val zone = safeZoneId(race.timezone)
        val correctEpoch = race.raceTimestamp.reanchorToRaceTimezone(race.timezone)
        val raceLocalDt = Instant.ofEpochMilli(correctEpoch).atZone(zone).toLocalDateTime()

        val date = raceLocalDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val targetHour = raceLocalDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00"))

        return dataSource.fetchTemperatureAtRaceTime(lat, lon, date, targetHour, race.timezone)
            .getOrNull()
    }
}
