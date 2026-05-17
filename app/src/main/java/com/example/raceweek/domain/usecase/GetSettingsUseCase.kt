package com.example.raceweek.domain.usecase

import com.example.raceweek.data.local.dao.SettingsDao
import com.example.raceweek.data.local.entity.SettingsEntity
import com.example.raceweek.domain.model.AppSettings
import com.example.raceweek.domain.model.NotificationTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsDao: SettingsDao
) {
    operator fun invoke(): Flow<AppSettings> =
        settingsDao.observe().map { it?.toDomain() ?: AppSettings() }
}

internal fun SettingsEntity.toDomain() = AppSettings(
    notifications = notifications == "T",
    time = NotificationTime.fromCode(time),
    practices = practices == "T",
    qualifyings = qualifyings == "T",
    races = races == "T"
)
