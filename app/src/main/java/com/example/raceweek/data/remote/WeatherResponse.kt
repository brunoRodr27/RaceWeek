package com.example.raceweek.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val hourly: WeatherHourly
)

@Serializable
data class WeatherHourly(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature2m: List<Double>
)
