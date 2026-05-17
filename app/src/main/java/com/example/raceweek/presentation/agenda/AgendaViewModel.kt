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
import com.example.raceweek.presentation.utils.toDeviceTimeString
import com.example.raceweek.presentation.utils.toSessionDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
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

    // Cada sessão (treino, qualifying, corrida) de cada UpcomingRace vira um
    // CalendarEvent no seu próprio dia UTC. A chave LocalDate suporta qualquer mês/ano.
    val calendarRaces: StateFlow<Map<LocalDate, List<CalendarEvent>>> = _upcomingRaces
        .map { races ->
            val byDate = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()
            for (race in races) {
                for (session in race.sessions) {
                    val date = Instant.ofEpochMilli(session.timestamp)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    byDate.getOrPut(date) { mutableListOf() }.add(
                        CalendarEvent(
                            flagResName = race.flagResName,
                            name = race.name,
                            time = session.timestamp.toDeviceTimeString(race.timezone),
                            series = race.categoryDescription,
                            sessionLabel = session.key.toSessionDisplayName(),
                            timestampMillis = session.timestamp
                        )
                    )
                }
            }
            byDate.mapValues { (_, list) -> list.sortedBy { it.timestampMillis } }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    init {
        viewModelScope.launch { runCatching { syncCategoriesUseCase() } }
        viewModelScope.launch { _heroRaceInfo.value = getNextRaceUseCase() }
        viewModelScope.launch { _upcomingRaces.value = getUpcomingRacesUseCase() }
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun refreshNextRace() {
        viewModelScope.launch { _heroRaceInfo.value = getNextRaceUseCase() }
    }

    fun getRaceById(id: String): UpcomingRace? = _upcomingRaces.value.find { it.id == id }
}
