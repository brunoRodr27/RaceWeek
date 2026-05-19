package com.example.raceweek.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor(
    private val client: HttpClient
) {
    suspend fun fetchTemperatureAtRaceTime(
        lat: Double,
        lon: Double,
        date: String,
        targetHour: String,
        timezone: String
    ): Result<String> {
        return try {
            val response: WeatherResponse = client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", lat)
                parameter("longitude", lon)
                parameter("hourly", "temperature_2m")
                parameter("start_date", date)
                parameter("end_date", date)
                parameter("timezone", timezone)
            }.body()

            val index = response.hourly.time.indexOfFirst { it == targetHour }
                .takeIf { it >= 0 }
                ?: return Result.failure(NoSuchElementException("Hora $targetHour não encontrada na resposta da API"))

            Result.success("%.1f°C".format(response.hourly.temperature2m[index]))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
