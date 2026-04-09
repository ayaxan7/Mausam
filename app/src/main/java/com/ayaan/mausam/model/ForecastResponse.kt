package com.ayaan.mausam.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list")    val list: List<ForecastItem>,
    @SerializedName("city")    val city: City
)

data class ForecastItem(
    @SerializedName("dt")         val dt: Long,
    @SerializedName("main")       val main: Main,
    @SerializedName("weather")    val weather: List<Weather>,
    @SerializedName("wind")       val wind: Wind,
    @SerializedName("dt_txt")     val dtTxt: String
)

data class City(
    @SerializedName("id")      val id: Int,
    @SerializedName("name")    val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset")  val sunset: Long
)
