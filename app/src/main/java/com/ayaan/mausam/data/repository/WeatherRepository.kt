package com.ayaan.mausam.data.repository

import com.ayaan.mausam.data.api.WeatherApiService
import com.ayaan.mausam.data.db.WeatherDao
import com.ayaan.mausam.data.db.WeatherHistoryEntity
import com.ayaan.mausam.model.ForecastResponse
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.util.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) {

    // ──────────────────────────────────────────────
    // Remote — current weather
    // ──────────────────────────────────────────────

    suspend fun getCurrentWeather(city: String): UiState<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeather(city)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                saveToHistory(body)
                UiState.Success(body)
            } else {
                val code = response.code()
                when (code) {
                    404 -> UiState.Error("City \"$city\" not found. Please check the spelling.")
                    401 -> UiState.Error("Invalid API key. Update it in Constants.kt.")
                    else -> UiState.Error("Server error ($code). Please try again.")
                }
            }
        } catch (e: java.net.UnknownHostException) {
            UiState.Error("No internet connection. Please check your network.")
        } catch (e: java.net.SocketTimeoutException) {
            UiState.Error("Request timed out. Please try again.")
        } catch (e: Exception) {
            UiState.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    suspend fun getCurrentWeatherByCoords(lat: Double, lon: Double): UiState<WeatherResponse> {
        return try {
            val response = apiService.getCurrentWeatherByCoords(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                saveToHistory(body)
                UiState.Success(body)
            } else {
                UiState.Error("Failed to fetch weather for your location (${response.code()}).")
            }
        } catch (e: java.net.UnknownHostException) {
            UiState.Error("No internet connection. Please check your network.")
        } catch (e: java.net.SocketTimeoutException) {
            UiState.Error("Request timed out. Please try again.")
        } catch (e: Exception) {
            UiState.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    // ──────────────────────────────────────────────
    // Remote — forecast
    // ──────────────────────────────────────────────

    suspend fun getForecast(city: String): UiState<ForecastResponse> {
        return try {
            val response = apiService.getForecast(city)
            if (response.isSuccessful && response.body() != null) {
                UiState.Success(response.body()!!)
            } else {
                UiState.Error("Failed to fetch forecast (${response.code()}).")
            }
        } catch (e: java.net.UnknownHostException) {
            UiState.Error("No internet connection. Please check your network.")
        } catch (e: java.net.SocketTimeoutException) {
            UiState.Error("Request timed out. Please try again.")
        } catch (e: Exception) {
            UiState.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    suspend fun getForecastByCoords(lat: Double, lon: Double): UiState<ForecastResponse> {
        return try {
            val response = apiService.getForecastByCoords(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                UiState.Success(response.body()!!)
            } else {
                UiState.Error("Failed to fetch forecast for your location (${response.code()}).")
            }
        } catch (e: java.net.UnknownHostException) {
            UiState.Error("No internet connection. Please check your network.")
        } catch (e: java.net.SocketTimeoutException) {
            UiState.Error("Request timed out. Please try again.")
        } catch (e: Exception) {
            UiState.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    // ──────────────────────────────────────────────
    // Local — history
    // ──────────────────────────────────────────────

    fun getWeatherHistory(): Flow<List<WeatherHistoryEntity>> = weatherDao.getAllHistory()

    suspend fun clearHistory() = weatherDao.clearHistory()

    // ──────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────

    private suspend fun saveToHistory(weather: WeatherResponse) {
        val entity = WeatherHistoryEntity(
            cityName    = "${weather.name}, ${weather.sys.country}",
            temperature = weather.main.temp,
            description = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
            iconCode    = weather.weather.firstOrNull()?.icon ?: "01d"
        )
        weatherDao.insertWeather(entity)
    }
}
