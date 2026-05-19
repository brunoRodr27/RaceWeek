package com.example.raceweek.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.domain.model.HeroRaceInfo
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.usecase.GetAllRacesUseCase
import com.example.raceweek.domain.usecase.GetCategoriesUseCase
import com.example.raceweek.domain.usecase.GetNextRaceUseCase
import com.example.raceweek.domain.usecase.ScheduleNotificationsUseCase
import com.example.raceweek.domain.usecase.SyncCategoriesUseCase
import com.example.raceweek.domain.util.reanchorToRaceTimezone
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
import java.time.ZoneId
import javax.inject.Inject

const val CATEGORY_ALL = "Todos"

@HiltViewModel
class AgendaViewModel @Inject constructor(
    getCategoriesUseCase: GetCategoriesUseCase,
    private val syncCategoriesUseCase: SyncCategoriesUseCase,
    private val getNextRaceUseCase: GetNextRaceUseCase,
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

        // Uma única leitura Firestore: todas as corridas servem tanto para o calendário
        // quanto para a agenda (upcoming = filtro por timestamp reanchorado) e HeroCard.
        viewModelScope.launch {
            val all = getAllRacesUseCase()
            val now = System.currentTimeMillis()
            _allRaces.value = all
            val upcoming = all.filter { it.raceTimestamp.reanchorToRaceTimezone(it.timezone) >= now }
            _upcomingRaces.value = upcoming
            _heroRaceInfo.value = getNextRaceUseCase(upcoming)
            _isLoading.value = false
            runCatching { scheduleNotificationsUseCase(upcoming) }
        }

        // Sempre que as categorias ativas mudarem, recalcula o HeroCard reaproveitando
        // as corridas já carregadas. drop(1) descarta a emissão inicial.
        viewModelScope.launch {
            activeCategoriesFlow.drop(1).collect {
                _heroRaceInfo.value = getNextRaceUseCase(_upcomingRaces.value.ifEmpty { null })
            }
        }
    }

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun refreshNextRace() {
        viewModelScope.launch {
            _heroRaceInfo.value = getNextRaceUseCase(_upcomingRaces.value.ifEmpty { null })
        }
    }

    fun getRaceById(id: String): UpcomingRace? = upcomingRaces.value.find { it.id == id }

    private fun buildCalendarMap(races: List<UpcomingRace>): Map<LocalDate, List<CalendarEvent>> {
        val byDate = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()
        for (race in races) {
            for (session in race.sessions) {
                // Reancora o timestamp pelo fuso real da corrida antes de calcular a data
                // no fuso do dispositivo — evita que sessões próximas à meia-noite apareçam
                // no dia errado do calendário.
                val correctedEpoch = session.timestamp.reanchorToRaceTimezone(race.timezone)
                val date = Instant.ofEpochMilli(correctedEpoch)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                byDate.getOrPut(date) { mutableListOf() }.add(
                    CalendarEvent(
                        flagResName = race.flagResName,
                        name = race.name,
                        time = session.timestamp.toDeviceTimeString(race.timezone),
                        series = race.categoryDescription,
                        sessionLabel = session.key.toSessionDisplayName(),
                        timestampMillis = correctedEpoch
                    )
                )
            }
        }
        return byDate.mapValues { (_, list) -> list.sortedBy { it.timestampMillis } }
    }
}
