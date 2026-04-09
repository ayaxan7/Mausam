package com.ayaan.mausam.viewmodel

import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.mausam.data.db.WeatherHistoryEntity
import com.ayaan.mausam.data.repository.WeatherRepository
import com.ayaan.mausam.model.ForecastResponse
import com.ayaan.mausam.model.ForecastItem
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(application) {

    // ──────────────────────────────────────────────
    // UI State Flows
    // ──────────────────────────────────────────────

    private val _weatherState = MutableStateFlow<UiState<WeatherResponse>>(UiState.Empty)
    val weatherState: StateFlow<UiState<WeatherResponse>> = _weatherState.asStateFlow()

    private val _forecastState = MutableStateFlow<UiState<ForecastResponse>>(UiState.Empty)
    val forecastState: StateFlow<UiState<ForecastResponse>> = _forecastState.asStateFlow()

    private val _historyState = MutableStateFlow<List<WeatherHistoryEntity>>(emptyList())
    val historyState: StateFlow<List<WeatherHistoryEntity>> = _historyState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ──────────────────────────────────────────────
    // Init — start collecting history
    // ──────────────────────────────────────────────

    init {
        viewModelScope.launch {
            repository.getWeatherHistory().collect { list ->
                _historyState.value = list
            }
        }
    }

    // ──────────────────────────────────────────────
    // Search query
    // ──────────────────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // ──────────────────────────────────────────────
    // Fetch by city name
    // ──────────────────────────────────────────────

    fun fetchWeatherByCity(city: String) {
        if (city.isBlank()) {
            _weatherState.value = UiState.Error("Please enter a city name.")
            return
        }
        viewModelScope.launch {
            _weatherState.value  = UiState.Loading
            _forecastState.value = UiState.Loading

            _weatherState.value  = repository.getCurrentWeather(city)
            _forecastState.value = repository.getForecast(city)
        }
    }

    // ──────────────────────────────────────────────
    // Fetch by GPS coordinates
    // ──────────────────────────────────────────────

    fun fetchWeatherByCoords(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherState.value  = UiState.Loading
            _forecastState.value = UiState.Loading

            _weatherState.value  = repository.getCurrentWeatherByCoords(lat, lon)
            _forecastState.value = repository.getForecastByCoords(lat, lon)

            // Update search bar with resolved city name
            if (_weatherState.value is UiState.Success) {
                val cityName = (_weatherState.value as UiState.Success<WeatherResponse>).data.name
                _searchQuery.value = cityName
            }
        }
    }

    // ──────────────────────────────────────────────
    // History
    // ──────────────────────────────────────────────

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    // ──────────────────────────────────────────────
    // Helpers — parse today's 3-hour forecast items
    // ──────────────────────────────────────────────

    fun getTodayForecast(forecast: ForecastResponse): List<ForecastItem> {
        val todayPrefix = forecast.list.firstOrNull()?.dtTxt?.substring(0, 10) ?: return emptyList()
        return forecast.list.filter { it.dtTxt.startsWith(todayPrefix) }
    }

    // Parse 5-day daily forecast (one entry per day — pick midday or first of day)
    fun getDailyForecast(forecast: ForecastResponse): List<ForecastItem> {
        return forecast.list
            .groupBy { it.dtTxt.substring(0, 10) }
            .map { (_, items) ->
                // Prefer the 12:00 slot; fall back to first slot of the day
                items.firstOrNull { it.dtTxt.contains("12:00") } ?: items.first()
            }
    }
}
