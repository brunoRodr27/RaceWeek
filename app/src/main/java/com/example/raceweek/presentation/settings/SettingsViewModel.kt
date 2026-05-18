package com.example.raceweek.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.AppSettings
import com.example.raceweek.domain.model.Category
import com.example.raceweek.domain.model.NotificationTime
import com.example.raceweek.domain.usecase.GetAllCategoriesUseCase
import com.example.raceweek.domain.usecase.GetSettingsUseCase
import com.example.raceweek.domain.usecase.ReorderCategoriesUseCase
import com.example.raceweek.domain.usecase.SaveSettingsUseCase
import com.example.raceweek.domain.usecase.ScheduleNotificationsUseCase
import com.example.raceweek.domain.usecase.UpdateCategoryStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingsUseCase: SaveSettingsUseCase,
    getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val updateCategoryStatusUseCase: UpdateCategoryStatusUseCase,
    private val reorderCategoriesUseCase: ReorderCategoriesUseCase,
    private val scheduleNotificationsUseCase: ScheduleNotificationsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = getSettingsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    val categories: StateFlow<List<Category>> = getAllCategoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setNotifications(enabled: Boolean) = save(settings.value.copy(notifications = enabled))
    fun setTime(time: NotificationTime) = save(settings.value.copy(time = time))
    fun setPractices(enabled: Boolean) = save(settings.value.copy(practices = enabled))
    fun setQualifyings(enabled: Boolean) = save(settings.value.copy(qualifyings = enabled))
    fun setRaces(enabled: Boolean) = save(settings.value.copy(races = enabled))

    fun toggleCategory(id: Int, active: Boolean) {
        viewModelScope.launch { updateCategoryStatusUseCase(id, active) }
    }

    fun reorderCategories(orderedIds: List<Int>) {
        viewModelScope.launch { reorderCategoriesUseCase(orderedIds) }
    }

    private fun save(updated: AppSettings) {
        viewModelScope.launch {
            saveSettingsUseCase(updated)
            runCatching { scheduleNotificationsUseCase() }
        }
    }
}
