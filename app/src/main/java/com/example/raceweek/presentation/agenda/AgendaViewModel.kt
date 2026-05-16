package com.example.raceweek.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.usecase.GetCategoriesUseCase
import com.example.raceweek.domain.usecase.GetNextRaceUseCase
import com.example.raceweek.domain.usecase.GetUpcomingRacesUseCase
import com.example.raceweek.domain.usecase.SyncCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    getCategoriesUseCase: GetCategoriesUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val getNextRaceUseCase: GetNextRaceUseCase,
    private val getUpcomingRacesUseCase: GetUpcomingRacesUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories: StateFlow<List<String>> = getCategoriesUseCase()
        .map { list -> listOf("Todos") + list.map { it.description } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf("Todos")
        )

    private val _heroRaceInfo = MutableStateFlow<HeroRaceInfo?>(null)
    val heroRaceInfo: StateFlow<HeroRaceInfo?> = _heroRaceInfo.asStateFlow()

    private val _upcomingRaces = MutableStateFlow<List<UpcomingRace>>(emptyList())
    val upcomingRaces: StateFlow<List<UpcomingRace>> = _upcomingRaces.asStateFlow()

    val calendarRaces: Map<Int, List<CalendarEvent>> = mapOf(
        4 to listOf(CalendarEvent("🇦🇿", "GP do Azerbaijão", "12:00", "F1")),
        11 to listOf(
            CalendarEvent("🇪🇸", "GP da Espanha", "15:00", "F1"),
            CalendarEvent("🇯🇵", "GP do Japão", "09:00", "MotoGP")
        ),
        25 to listOf(
            CalendarEvent("🇲🇨", "GP de Mônaco", "14:00", "F1"),
            CalendarEvent("🇺🇸", "Indy 500", "18:00", "IndyCar"),
            CalendarEvent("🇺🇸", "Coca-Cola 600", "19:00", "NASCAR")
        ),
        31 to listOf(CalendarEvent("🇩🇪", "E-Prix de Berlim", "15:00", "Formula E"))
    )

    init {
        viewModelScope.launch { runCatching { syncCategoriesUseCase() } }
        viewModelScope.launch { _heroRaceInfo.value = getNextRaceUseCase() }
        viewModelScope.launch { _upcomingRaces.value = getUpcomingRacesUseCase() }
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun getRaceById(id: String): UpcomingRace? = _upcomingRaces.value.find { it.id == id }
}
