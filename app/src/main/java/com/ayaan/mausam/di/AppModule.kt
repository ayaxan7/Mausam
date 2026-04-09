package com.ayaan.mausam.di

import android.content.Context
import androidx.room.Room
import com.ayaan.mausam.data.api.PlacesApiService
import com.ayaan.mausam.data.api.WeatherApiService
import com.ayaan.mausam.data.db.WeatherDao
import com.ayaan.mausam.data.db.WeatherDatabase
import com.ayaan.mausam.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ──────────────────────────────────────────────
    // Networking
    // ──────────────────────────────────────────────

    @Provides
    @Singleton
    @Named("weatherClient")
    fun provideWeatherOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("nominatimClient")
    fun provideNominatimOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", Constants.OSM_USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("weatherRetrofit")
    fun provideWeatherRetrofit(@Named("weatherClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("nominatimRetrofit")
    fun provideNominatimRetrofit(@Named("nominatimClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.OSM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApiService(@Named("weatherRetrofit") retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun providePlacesApiService(@Named("nominatimRetrofit") retrofit: Retrofit): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)

    // ──────────────────────────────────────────────
    // Database
    // ──────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideWeatherDao(database: WeatherDatabase): WeatherDao =
        database.weatherDao()
}
