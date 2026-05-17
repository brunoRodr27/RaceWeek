package com.example.raceweek.domain.model

enum class NotificationTime(val code: String, val label: String) {
    TWO_HOURS("A", "2 horas"),
    ONE_HOUR("B", "1 hora"),
    THIRTY_MIN("C", "30 min");

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
