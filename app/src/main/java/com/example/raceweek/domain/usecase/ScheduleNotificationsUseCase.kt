package com.example.raceweek.domain.usecase

import com.example.raceweek.data.local.dao.SettingsDao
import com.example.raceweek.data.local.entity.SettingsEntity
import com.example.raceweek.data.notification.NotificationScheduler
import com.example.raceweek.domain.model.UpcomingRace
import javax.inject.Inject

class ScheduleNotificationsUseCase @Inject constructor(
    private val getUpcomingRacesUseCase: GetUpcomingRacesUseCase,
    private val settingsDao: SettingsDao,
    private val scheduler: NotificationScheduler
) {
    private enum class SessionType { PRACTICE, QUALIFYING, RACE }

    suspend operator fun invoke(preloadedRaces: List<UpcomingRace>? = null) {
        val races = preloadedRaces ?: getUpcomingRacesUseCase()

        races.forEach { race ->
            race.sessions.forEach { session ->
                scheduler.cancel(notifId(race.id, session.key))
            }
        }

        val settings = settingsDao.get() ?: SettingsEntity()
        if (settings.notifications != "T") return

        val advanceMillis = when (settings.time) {
            "A" -> 7_200_000L
            "C" -> 1_800_000L
            else -> 3_600_000L
        }
        val advanceLabel = when (settings.time) {
            "A" -> "2 horas"
            "C" -> "30 min"
            else -> "1 hora"
        }
        val now = System.currentTimeMillis()

        races.forEach { race ->
            race.sessions.forEach { session ->
                val type = sessionType(session.key)
                val enabled = when (type) {
                    SessionType.PRACTICE -> settings.practices == "T"
                    SessionType.QUALIFYING -> settings.qualifyings == "T"
                    SessionType.RACE -> settings.races == "T"
                }
                if (!enabled) return@forEach

                val triggerAt = session.timestamp - advanceMillis
                if (triggerAt <= now) return@forEach

                scheduler.schedule(
                    id = notifId(race.id, session.key),
                    triggerAtMillis = triggerAt,
                    title = race.name,
                    body = "${session.key.toSessionLabel()} · em $advanceLabel"
                )
            }
        }
    }

    private fun notifId(raceId: String, sessionKey: String): Int =
        (raceId + "_" + sessionKey).hashCode()

    private fun sessionType(key: String): SessionType = when {
        key.startsWith("practice") -> SessionType.PRACTICE
        key.contains("qualifying") -> SessionType.QUALIFYING
        else -> SessionType.RACE
    }

    private fun String.toSessionLabel(): String = when (this.lowercase()) {
        "practice" -> "Treino Livre"
        "practiceone" -> "Treino Livre 1"
        "practicetwo" -> "Treino Livre 2"
        "practicethree" -> "Treino Livre 3"
        "qualifying" -> "Classificação"
        "sprintqualifying" -> "Classificação Sprint"
        "sprint" -> "Corrida Sprint"
        "race" -> "Corrida"
        else -> replaceFirstChar { it.uppercase() }
    }
}
