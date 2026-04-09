package com.ayaan.mausam.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("id")         val id: Int,
    @SerializedName("name")       val name: String,
    @SerializedName("main")       val main: Main,
    @SerializedName("weather")    val weather: List<Weather>,
    @SerializedName("wind")       val wind: Wind,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("sys")        val sys: Sys,
    @SerializedName("dt")         val dt: Long,
    @SerializedName("coord")      val coord: Coord
)

data class Main(
    @SerializedName("temp")       val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min")   val tempMin: Double,
    @SerializedName("temp_max")   val tempMax: Double,
    @SerializedName("pressure")   val pressure: Int,
    @SerializedName("humidity")   val humidity: Int
)

data class Weather(
    @SerializedName("id")          val id: Int,
    @SerializedName("main")        val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon")        val icon: String
)

data class Wind(
    @SerializedName("speed") val speed: Double,
    @SerializedName("deg")   val deg: Int
)

data class Sys(
    @SerializedName("country") val country: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset")  val sunset: Long
)

data class Coord(
    @SerializedName("lon") val lon: Double,
    @SerializedName("lat") val lat: Double
)
