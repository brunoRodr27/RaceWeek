package com.example.raceweek.presentation.agenda

import androidx.lifecycle.ViewModel
import com.example.raceweek.domain.model.CalendarEvent
import com.example.raceweek.domain.model.Race
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor() : ViewModel() {

    val categories = listOf("Todos", "Formula 1", "MotoGP", "IndyCar", "Formula E", "WEC", "NASCAR")

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val races = listOf(
        Race(id = "monaco_f1", category = "Formula 1", flag = "🇲🇨", name = "GP de Mônaco",
            location = "Circuit de Monaco · Monte Carlo", date = "Dom, 25 Mai",
            time = "14:00", weatherIcon = "☀️", temperature = "22°C", isHero = true),
        Race(id = "indy500", category = "IndyCar", flag = "🇺🇸", name = "Indianapolis 500",
            location = "Indianapolis Motor Speedway", date = "Dom, 25 Mai",
            time = "18:00", weatherIcon = "⛅", temperature = "24°C"),
        Race(id = "berlin_fe", category = "Formula E", flag = "🇩🇪", name = "E-Prix de Berlim",
            location = "Tempelhof Airport Street Circuit", date = "Sáb, 31 Mai",
            time = "15:00", weatherIcon = "🌥️", temperature = "18°C"),
        Race(id = "canada_f1", category = "Formula 1", flag = "🇨🇦", name = "GP do Canadá",
            location = "Circuit Gilles Villeneuve", date = "Dom, 15 Jun",
            time = "14:00", weatherIcon = "☀️", temperature = "21°C"),
        Race(id = "lemans_wec", category = "WEC", flag = "🇫🇷", name = "24h de Le Mans",
            location = "Circuit de la Sarthe", date = "Sáb, 14 Jun",
            time = "16:00", weatherIcon = "🌧️", temperature = "17°C"),
        Race(id = "italy_motogp", category = "MotoGP", flag = "🇮🇹", name = "GP da Itália",
            location = "Mugello Circuit · Toscana", date = "Dom, 1 Jun",
            time = "14:00", weatherIcon = "☀️", temperature = "26°C"),
        Race(id = "cola600_nascar", category = "NASCAR", flag = "🇺🇸", name = "Coca-Cola 600",
            location = "Charlotte Motor Speedway", date = "Dom, 25 Mai",
            time = "19:00", weatherIcon = "⛅", temperature = "27°C"),
        Race(id = "london_fe", category = "Formula E", flag = "🇬🇧", name = "E-Prix de Londres",
            location = "ExCeL Circuit · London", date = "Sáb, 28 Jun",
            time = "16:00", weatherIcon = "🌥️", temperature = "16°C"),
        Race(id = "detroit_indycar", category = "IndyCar", flag = "🇺🇸", name = "Detroit Grand Prix",
            location = "Belle Isle Street Circuit", date = "Dom, 1 Jun",
            time = "14:30", weatherIcon = "⛅", temperature = "22°C"),
    )

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

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun filteredRaces(cat: String): List<Race> =
        if (cat == "Todos") races else races.filter { it.category == cat }

    fun getRaceById(id: String): Race? = races.find { it.id == id }
}
