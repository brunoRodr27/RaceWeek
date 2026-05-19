package com.example.raceweek.domain.model

enum class NotificationTime(val code: String) {
    TWO_HOURS("A"),
    ONE_HOUR("B"),
    THIRTY_MIN("C");

    companion object {
        fun fromCode(code: String) = entries.find { it.code == code } ?: ONE_HOUR
    }
}

data class AppSettings(
    val notifications: Boolean = true,
    val time: NotificationTime = NotificationTime.ONE_HOUR,
    val practices: Boolean = true,
    val qualifyings: Boolean = true,
    val races: Boolean = true
)
