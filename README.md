# Weather History Tracker (Mausam)

Mausam is a Kotlin + Jetpack Compose Android weather app with history tracking, current location support, and smart city search.

## Features

- Search weather by city name.
- City autocomplete powered by OpenStreetMap Nominatim.
- Suggestion selection uses stored coordinates to fetch weather/forecast for the exact place.
- `Go`/IME search prefers selected suggestion coordinates; otherwise falls back to city-name lookup.
- Current-location weather with runtime permission checks.
- Device location-services check with prompt to open system Location Settings when disabled.
- Weather history saved locally with Room.

## Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository pattern
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + Gson + OkHttp
- **Weather API**: OpenWeatherMap
- **Places API**: OpenStreetMap Nominatim
- **Local Database**: Room
- **Concurrency**: Kotlin Coroutines + Flow
- **Location**: FusedLocationProviderClient (Google Play Services)
- **Images**: Coil

## Setup

### 1) Requirements

- Android Studio (recent stable version)
- Java 17

### 2) Configure OpenWeatherMap API key

Get a key from [OpenWeatherMap](https://openweathermap.org/api), then add it to the project root `local.properties`:

```properties
OPEN_WEATHER_API_KEY=YOUR_ACTUAL_API_KEY_HERE
```

This key is injected into `BuildConfig.OPEN_WEATHER_API_KEY` during build.

> Build note: the app build is configured to fail fast if `OPEN_WEATHER_API_KEY` is missing.

### 3) Run

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run on emulator/device.

## Search and Location Behavior

- Typing 2+ characters shows Nominatim place suggestions.
- Selecting a suggestion immediately fetches weather using that suggestion's latitude/longitude.
- Pressing `Go` after selecting a suggestion keeps using the selected coordinates.
- If no suggestion is selected, `Go` performs normal city-name weather lookup.
- On app launch, location permission is requested (if needed) and current location weather is fetched.
- If device location is disabled, the app shows a dialog and can take the user to system location settings.

## Error Handling

- Invalid OpenWeatherMap key (`401`) shows a clear message to set/update `OPEN_WEATHER_API_KEY`.
- Offline/timeout/server errors are surfaced as user-friendly messages.

## Build Notes

- Project uses `compileSdk = 35`.
- `gradle.properties` includes:

```properties
android.suppressUnsupportedCompileSdk=35
```

This suppresses AGP's unsupported compile SDK warning for current tooling.

## Architecture Overview

- **Data layer**: Retrofit services + Room DAO + `WeatherRepository`.
- **Presentation layer**: `WeatherViewModel` exposes `StateFlow` for weather, forecast, history, query, and suggestions.
- **UI layer**: Compose screens/components consume state and dispatch user actions.

## Screens

- **Home**: Search, autocomplete, current weather, today's forecast, 5-day forecast, and current-location action.
- **History**: Previously fetched weather entries stored locally.
