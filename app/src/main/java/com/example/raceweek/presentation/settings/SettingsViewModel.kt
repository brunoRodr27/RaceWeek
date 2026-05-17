package com.example.raceweek.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.AppSettings
import com.example.raceweek.domain.model.NotificationTime
import com.example.raceweek.domain.usecase.GetSettingsUseCase
import com.example.raceweek.domain.usecase.SaveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setNotifications(enabled: Boolean) = save(settings.value.copy(notifications = enabled))
    fun setTime(time: NotificationTime) = save(settings.value.copy(time = time))
    fun setPractices(enabled: Boolean) = save(settings.value.copy(practices = enabled))
    fun setQualifyings(enabled: Boolean) = save(settings.value.copy(qualifyings = enabled))
    fun setRaces(enabled: Boolean) = save(settings.value.copy(races = enabled))

    private fun save(updated: AppSettings) {
        viewModelScope.launch { saveSettingsUseCase(updated) }
    }
}
