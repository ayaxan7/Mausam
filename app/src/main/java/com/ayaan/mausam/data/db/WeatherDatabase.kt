package com.ayaan.mausam.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}
