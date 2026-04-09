package com.ayaan.mausam.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ayaan.mausam.model.ForecastResponse
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.ui.components.DailyForecastItem
import com.ayaan.mausam.ui.components.ForecastItem
import com.ayaan.mausam.ui.components.SearchBar
import com.ayaan.mausam.ui.components.WeatherCard
import com.ayaan.mausam.util.UiState
import com.ayaan.mausam.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val context      = LocalContext.current
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastState.collectAsStateWithLifecycle()
    val searchQuery  by viewModel.searchQuery.collectAsStateWithLifecycle()

    // ──── Permission launcher ────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocationAndWeather(context, viewModel)
        }
    }

    // ──── Auto-fetch on first launch ────────────────────────────────────────
    LaunchedEffect(Unit) {
        val hasFine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchLocationAndWeather(context, viewModel)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ──── Background gradient ───────────────────────────────────────────────
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D1B2A), Color(0xFF1B2A3B), Color(0xFF0A1929))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mausam",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Weather History Tracker",
                            color = Color(0xFF90CAF9),
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = onNavigateToHistory,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = Color.White
                        )
                    }
                }
            }

            // Search bar
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = { viewModel.fetchWeatherByCity(searchQuery) }
                )
            }

            // Location button
            item {
                OutlinedButton(
                    onClick = {
                        val hasFine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasFine || hasCoarse) {
                            fetchLocationAndWeather(context, viewModel)
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF90CAF9)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color(0xFF90CAF9).copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Use My Location")
                }
            }

            // Weather content
            when (val state = weatherState) {
                is UiState.Loading -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF64B5F6))
                    }
                }

                is UiState.Success -> {
                    item { WeatherCard(weather = state.data) }

                    // Today's forecast section
                    if (forecastState is UiState.Success) {
                        val forecast = (forecastState as UiState.Success<ForecastResponse>).data
                        val todayItems  = viewModel.getTodayForecast(forecast)
                        val dailyItems  = viewModel.getDailyForecast(forecast)

                        if (todayItems.isNotEmpty()) {
                            item {
                                SectionHeader(title = "Today's Forecast")
                            }
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                ) {
                                    items(todayItems) { fi ->
                                        ForecastItem(item = fi)
                                    }
                                }
                            }
                        }

                        if (dailyItems.isNotEmpty()) {
                            item { SectionHeader(title = "5-Day Forecast") }
                            items(dailyItems) { fi ->
                                DailyForecastItem(item = fi)
                            }
                        }
                    }

                    if (forecastState is UiState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF64B5F6),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> item {
                    ErrorCard(message = state.message)
                }

                is UiState.Empty -> item {
                    EmptyPrompt()
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ──── Private helpers ────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
private fun fetchLocationAndWeather(context: Context, viewModel: WeatherViewModel) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                viewModel.fetchWeatherByCoords(location.latitude, location.longitude)
            } else {
                // Last known as fallback
                fusedClient.lastLocation.addOnSuccessListener { last ->
                    if (last != null) {
                        viewModel.fetchWeatherByCoords(last.latitude, last.longitude)
                    }
                }
            }
        }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D).copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                tint = Color(0xFFFF8A80),
                modifier = Modifier.size(28.dp)
            )
            Text(text = message, color = Color(0xFFFF8A80), fontSize = 14.sp)
        }
    }
}

@Composable
private fun EmptyPrompt() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFF90CAF9).copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "Search for a city or\nallow location access",
            color = Color(0xFF90CAF9).copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
