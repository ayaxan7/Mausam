# Weather History Tracker (Mausam)

A modern, clean architecture Android application that tracks weather history, built with Kotlin, Jetpack Compose, and Hilt.

## Tech Stack
* **UI**: Jetpack Compose
* **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture Principles
* **Dependency Injection**: Hilt
* **Networking**: Retrofit + Gson + OkHttp
* **Local Database**: Room
* **Concurrency**: Kotlin Coroutines + Flow
* **Images**: Coil
* **Location Processing**: FusedLocationProviderClient (Google Play Services)

## Setup Instructions

### 1. Requirements
* Android Studio Iguana / Jellyfish or newer
* Java 17

### 2. Getting the OpenWeatherMap API Key
1. Go to [OpenWeatherMap](https://openweathermap.org/api) and sign up for a free account.
2. Navigate to your profile and find "My API keys".
3. Generate a new key.

### 3. Adding the API Key to the Project
Add your key to `local.properties` (project root):

```properties
OPEN_WEATHER_API_KEY=YOUR_ACTUAL_API_KEY_HERE
```

This value is injected into `BuildConfig.OPEN_WEATHER_API_KEY` during build and is not stored in source files.

### 4. Running the Project
1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync the project fully.
4. Select your emulator or connected device and run the project (Shift + F10 or the green play button).

## Architecture Details

The application follows the **MVVM** pattern with robust separation of concerns using **Hilt** for Dependency Injection.

### Data Layer
* **API Service**: Defines Retrofit endpoints for fetching current weather and 5-day forecasts.
* **Database (Room)**: Defines entities and Data Access Objects (DAOs) to persist search history locally.
* **Repository**: The `WeatherRepository` acts as the single source of truth. It handles data fetching, maps network responses into `UiState<T>` sealed classes (simplifying UI logic), and delegates successful history saves to the Room DAO automatically.

### UI Layer
* **ViewModel**: `WeatherViewModel` orchestrates requests from the UI. It provides hot `StateFlow` sources to the Composable screens, exposing discrete states like `Loading`, `Success`, `Error`, and `Empty`.
* **Jetpack Compose Screens**: Reusable, stateless components mapped directly to the active state using unidirectional data flow. This ensures a predictable, snappy user interface.

