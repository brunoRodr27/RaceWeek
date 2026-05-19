package com.example.raceweek.domain.usecase

import android.content.Context
import com.example.raceweek.R
import com.example.raceweek.data.notification.NotificationScheduler
import com.example.raceweek.domain.model.NotificationTime
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.util.reanchorToRaceTimezone
import com.example.raceweek.domain.util.toSessionLabel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ScheduleNotificationsUseCase @Inject constructor(
    private val getUpcomingRacesUseCase: GetUpcomingRacesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val scheduler: NotificationScheduler,
    @ApplicationContext private val context: Context
) {
    private enum class SessionType { PRACTICE, QUALIFYING, RACE }

    suspend operator fun invoke(preloadedRaces: List<UpcomingRace>? = null) {
        val races = preloadedRaces ?: getUpcomingRacesUseCase()

        races.forEach { race ->
            race.sessions.forEach { session ->
                scheduler.cancel(notifId(race.id, session.key))
            }
        }

        val settings = getSettingsUseCase().first()
        if (!settings.notifications) return

        val advanceMillis = when (settings.time) {
            NotificationTime.TWO_HOURS  -> 7_200_000L
            NotificationTime.THIRTY_MIN -> 1_800_000L
            NotificationTime.ONE_HOUR   -> 3_600_000L
        }
        val advanceLabel = when (settings.time) {
            NotificationTime.TWO_HOURS  -> context.getString(R.string.notif_advance_2h)
            NotificationTime.THIRTY_MIN -> context.getString(R.string.notif_advance_30min)
            NotificationTime.ONE_HOUR   -> context.getString(R.string.notif_advance_1h)
        }
        val now = System.currentTimeMillis()

        races.forEach { race ->
            race.sessions.forEach { session ->
                val type = sessionType(session.key)
                val enabled = when (type) {
                    SessionType.PRACTICE   -> settings.practices
                    SessionType.QUALIFYING -> settings.qualifyings
                    SessionType.RACE       -> settings.races
                }
                if (!enabled) return@forEach

                val triggerAt = session.timestamp.reanchorToRaceTimezone(race.timezone) - advanceMillis
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
        key.startsWith("practice")      -> SessionType.PRACTICE
        key.contains("qualifying")      -> SessionType.QUALIFYING
        else                            -> SessionType.RACE
    }
}
