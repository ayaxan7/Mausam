package com.ayaan.mausam.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ayaan.mausam.util.Constants.WEATHER_HISTORY_DB

@Entity(tableName = WEATHER_HISTORY_DB)
data class WeatherHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val description: String,
    val iconCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
