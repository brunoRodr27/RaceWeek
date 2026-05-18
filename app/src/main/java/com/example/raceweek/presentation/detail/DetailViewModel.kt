package com.example.raceweek.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raceweek.domain.model.UpcomingRace
import com.example.raceweek.domain.usecase.GetRaceWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getRaceWeatherUseCase: GetRaceWeatherUseCase
) : ViewModel() {

    private val _weather = MutableStateFlow<String?>(null)
    val weather: StateFlow<String?> = _weather.asStateFlow()

    fun loadWeather(race: UpcomingRace) {
        if (_weather.value != null) return
        viewModelScope.launch {
            _weather.value = getRaceWeatherUseCase(race) ?: "–"
        }
    }
}
