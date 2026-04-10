package com.ayaan.mausam.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.mausam.data.db.WeatherHistoryEntity
import com.ayaan.mausam.model.PlaceSuggestion
import com.ayaan.mausam.data.repository.WeatherRepository
import com.ayaan.mausam.model.ForecastResponse
import com.ayaan.mausam.model.ForecastItem
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository
) : AndroidViewModel(application) {
    private val _weatherState = MutableStateFlow<UiState<WeatherResponse>>(UiState.Empty)
    val weatherState: StateFlow<UiState<WeatherResponse>> = _weatherState.asStateFlow()

    private val _forecastState = MutableStateFlow<UiState<ForecastResponse>>(UiState.Empty)
    val forecastState: StateFlow<UiState<ForecastResponse>> = _forecastState.asStateFlow()

    private val _historyState = MutableStateFlow<List<WeatherHistoryEntity>>(emptyList())
    val historyState: StateFlow<List<WeatherHistoryEntity>> = _historyState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _citySuggestions = MutableStateFlow<List<PlaceSuggestion>>(emptyList())
    val citySuggestions: StateFlow<List<PlaceSuggestion>> = _citySuggestions.asStateFlow()

    private val _isSuggestionsLoading = MutableStateFlow(false)
    val isSuggestionsLoading: StateFlow<Boolean> = _isSuggestionsLoading.asStateFlow()

    private val _displayLocationLabel = MutableStateFlow<String?>(null)
    val displayLocationLabel: StateFlow<String?> = _displayLocationLabel.asStateFlow()

    private var suppressAutocomplete = false

    init {
        viewModelScope.launch {
            repository.getWeatherHistory().collect { list ->
                _historyState.value = list
            }
        }

        viewModelScope.launch {
            _searchQuery
                .debounce(350)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (suppressAutocomplete) {
                        suppressAutocomplete = false
                        return@collectLatest
                    }

                    val normalizedQuery = query.trim()
                    if (normalizedQuery.length < 2) {
                        _citySuggestions.value = emptyList()
                        _isSuggestionsLoading.value = false
                        return@collectLatest
                    }

                    _isSuggestionsLoading.value = true
                    when (val result = repository.searchCities(normalizedQuery)) {
                        is UiState.Success -> _citySuggestions.value = result.data
                        else -> _citySuggestions.value = emptyList()
                    }
                    _isSuggestionsLoading.value = false
                }
        }
    }
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearSuggestions() {
        _citySuggestions.value = emptyList()
        _isSuggestionsLoading.value = false
    }

    fun onSuggestionSelected(suggestion: PlaceSuggestion) {
        suppressAutocomplete = true
        _searchQuery.value = suggestion.title
        clearSuggestions()
        fetchWeatherByCoords(
            suggestion.latitude,
            suggestion.longitude,
            displayLocationName = suggestion.title
        )
    }

    fun fetchWeatherByCity(city: String) {
        if (city.isBlank()) {
            _weatherState.value = UiState.Error("Please enter a city name.")
            return
        }
        viewModelScope.launch {
            clearSuggestions()
            _displayLocationLabel.value = city
            _weatherState.value  = UiState.Loading
            _forecastState.value = UiState.Loading

            _weatherState.value  = repository.getCurrentWeather(city)
            _forecastState.value = repository.getForecast(city)
        }
    }

    fun fetchWeatherByCoords(lat: Double, lon: Double, displayLocationName: String? = null) {
        viewModelScope.launch {
            _weatherState.value  = UiState.Loading
            _forecastState.value = UiState.Loading

            _weatherState.value  = repository.getCurrentWeatherByCoords(lat, lon, displayLocationName)
            _forecastState.value = repository.getForecastByCoords(lat, lon)

            val resolvedLabel = displayLocationName ?: when (val weather = _weatherState.value) {
                is UiState.Success -> weather.data.name
                else -> null
            }
            _displayLocationLabel.value = resolvedLabel
        }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

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
