package com.ayaan.mausam.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entity: WeatherHistoryEntity)

    @Query("SELECT * FROM weather_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<WeatherHistoryEntity>>

    @Query("DELETE FROM weather_history")
    suspend fun clearHistory()
}
