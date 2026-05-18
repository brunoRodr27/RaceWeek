package com.example.raceweek

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.raceweek.data.notification.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaceWeekApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "RaceWeek",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de sessões de corrida"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
