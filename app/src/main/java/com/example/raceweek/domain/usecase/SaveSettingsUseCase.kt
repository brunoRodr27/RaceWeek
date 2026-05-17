package com.example.raceweek.domain.usecase

import com.example.raceweek.data.local.dao.SettingsDao
import com.example.raceweek.data.local.entity.SettingsEntity
import com.example.raceweek.domain.model.AppSettings
import javax.inject.Inject

class SaveSettingsUseCase @Inject constructor(
    private val settingsDao: SettingsDao
) {
    suspend operator fun invoke(settings: AppSettings) {
        settingsDao.upsert(
            SettingsEntity(
                id = 1,
                notifications = if (settings.notifications) "T" else "F",
                time = settings.time.code,
                practices = if (settings.practices) "T" else "F",
                qualifyings = if (settings.qualifyings) "T" else "F",
                races = if (settings.races) "T" else "F"
            )
        )
    }
}
