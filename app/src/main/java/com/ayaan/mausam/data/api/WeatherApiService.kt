package com.ayaan.mausam.data.api

import com.ayaan.mausam.model.ForecastResponse
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q")     cityName: String,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = Constants.UNITS
    ): Response<WeatherResponse>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoords(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = Constants.UNITS
    ): Response<WeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q")     cityName: String,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = Constants.UNITS
    ): Response<ForecastResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecastByCoords(
        @Query("lat")   lat: Double,
        @Query("lon")   lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String = Constants.UNITS
    ): Response<ForecastResponse>
}
