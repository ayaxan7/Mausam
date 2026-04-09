package com.ayaan.mausam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ayaan.mausam.model.WeatherResponse
import com.ayaan.mausam.util.Constants
import kotlin.math.roundToInt

@Composable
fun WeatherCard(
    weather: WeatherResponse,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF0D47A1), Color(0xFF01579B))
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = gradientBrush)
                .padding(24.dp)
        ) {
            Column {
                // City + Country
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF90CAF9),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${weather.name}, ${weather.sys.country}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Icon + Temperature
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "${weather.main.temp.roundToInt()}°C",
                            color = Color.White,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 72.sp
                        )
                        Text(
                            text = weather.weather.firstOrNull()?.description
                                ?.replaceFirstChar { it.uppercase() } ?: "",
                            color = Color(0xFFBBDEFB),
                            fontSize = 16.sp
                        )
                    }

                    AsyncImage(
                        model = Constants.ICON_URL.format(
                            weather.weather.firstOrNull()?.icon ?: "01d"
                        ),
                        contentDescription = "Weather icon",
                        modifier = Modifier.size(96.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Min / Max
                Text(
                    text = "↑ ${weather.main.tempMax.roundToInt()}°  ↓ ${weather.main.tempMin.roundToInt()}°",
                    color = Color(0xFFBBDEFB),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(16.dp))

                // Extra info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherStat(
                        icon  = Icons.Default.Opacity,
                        label = "Humidity",
                        value = "${weather.main.humidity}%"
                    )
                    WeatherStat(
                        icon  = Icons.Default.Air,
                        label = "Wind",
                        value = "${weather.wind.speed.roundToInt()} m/s"
                    )
                    WeatherStat(
                        icon  = Icons.Default.Visibility,
                        label = "Visibility",
                        value = "${weather.visibility / 1000} km"
                    )
                    WeatherStat(
                        icon  = Icons.Default.Thermostat,
                        label = "Feels like",
                        value = "${weather.main.feelsLike.roundToInt()}°C"
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStat(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF90CAF9),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(text = label, color = Color(0xFFBBDEFB), fontSize = 11.sp)
    }
}
