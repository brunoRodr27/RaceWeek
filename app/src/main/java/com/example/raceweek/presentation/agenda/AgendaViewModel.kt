package com.example.raceweek.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.usecase.GetAllRacesUseCase
import com.example.raceweek.domain.usecase.GetCategoriesUseCase
import com.example.raceweek.domain.usecase.GetNextRaceUseCase
import com.example.raceweek.domain.usecase.GetUpcomingRacesUseCase
import com.example.raceweek.domain.usecase.ScheduleNotificationsUseCase
import com.example.raceweek.domain.usecase.SyncCategoriesUseCase
import com.example.raceweek.presentation.utils.toDeviceTimeString
import com.example.raceweek.presentation.utils.toSessionDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

const val CATEGORY_ALL = "Todos"

@HiltViewModel
class AgendaViewModel @Inject constructor(
    getCategoriesUseCase: GetCategoriesUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val getNextRaceUseCase: GetNextRaceUseCase,
    private val getUpcomingRacesUseCase: GetUpcomingRacesUseCase,
    private val getAllRacesUseCase: GetAllRacesUseCase,
    private val scheduleNotificationsUseCase: ScheduleNotificationsUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Fonte compartilhada: categorias ativas do banco, reativa a mudanças nas configurações.
    // Eagerly: a query à Room começa junto com o ViewModel, garantindo que o buffer replay=1
    // já esteja populado antes de qualquer combine se inscrever — evita race condition.
    private val activeCategoriesFlow = getCategoriesUseCase()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val categories: StateFlow<List<String>> = activeCategoriesFlow
        .map { list -> listOf(CATEGORY_ALL) + list.map { it.description } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf(CATEGORY_ALL)
        )

    // Conjunto de descriptions ativas, usado para filtrar corridas reativamente.
    private val activeDescriptions = activeCategoriesFlow
        .map { list -> list.map { it.description }.toSet() }

    private val _heroRaceInfo = MutableStateFlow<HeroRaceInfo?>(null)
    val heroRaceInfo: StateFlow<HeroRaceInfo?> = _heroRaceInfo.asStateFlow()

    private val _upcomingRaces = MutableStateFlow<List<UpcomingRace>>(emptyList())
    private val _allRaces = MutableStateFlow<List<UpcomingRace>>(emptyList())

    // Corridas futuras filtradas pelas categorias ativas no momento.
    val upcomingRaces: StateFlow<List<UpcomingRace>> = combine(
        activeDescriptions, _upcomingRaces
    ) { activeCats, races ->
        races.filter { it.categoryDescription in activeCats }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // Calendário completo (passadas + futuras) filtrado pelas categorias ativas.
    val calendarRaces: StateFlow<Map<LocalDate, List<CalendarEvent>>> = combine(
        activeDescriptions, _allRaces
    ) { activeCats, races ->
        buildCalendarMap(races.filter { it.categoryDescription in activeCats })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    init {
        viewModelScope.launch { runCatching { syncCategoriesUseCase() } }
        viewModelScope.launch { _heroRaceInfo.value = getNextRaceUseCase() }
        viewModelScope.launch { _allRaces.value = getAllRacesUseCase() }
        // Carrega corridas e agenda notificações com os mesmos dados — evita segunda chamada
        // ao Firestore que poderia retornar vazia por race condition na inicialização.
        viewModelScope.launch {
            val races = getUpcomingRacesUseCase()
            _upcomingRaces.value = races
            runCatching { scheduleNotificationsUseCase(races) }
        }
        // Sempre que as categorias ativas mudarem, rebusca o próximo evento do HeroCard.
        // drop(1) descarta a emissão inicial para não duplicar a busca já feita acima.
        viewModelScope.launch {
            activeCategoriesFlow.drop(1).collect {
                _heroRaceInfo.value = getNextRaceUseCase()
            }
        }
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun refreshNextRace() {
        viewModelScope.launch { _heroRaceInfo.value = getNextRaceUseCase() }
    }

    fun getRaceById(id: String): UpcomingRace? = upcomingRaces.value.find { it.id == id }

    private fun buildCalendarMap(races: List<UpcomingRace>): Map<LocalDate, List<CalendarEvent>> {
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
        return byDate.mapValues { (_, list) -> list.sortedBy { it.timestampMillis } }
    }
}
