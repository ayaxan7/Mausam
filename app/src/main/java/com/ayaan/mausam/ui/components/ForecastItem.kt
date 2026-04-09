package com.ayaan.mausam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ayaan.mausam.model.ForecastItem
import com.ayaan.mausam.util.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ForecastItem(
    item: ForecastItem,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
    val time = sdf.format(Date(item.dt * 1000L))

    Card(
        modifier = modifier
            .width(90.dp)
            .height(130.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = time, color = Color(0xFFBBDEFB), fontSize = 13.sp)
            AsyncImage(
                model = Constants.ICON_URL.format(
                    item.weather.firstOrNull()?.icon ?: "01d"
                ),
                contentDescription = "Forecast icon",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${item.main.temp.roundToInt()}°C",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DailyForecastItem(
    item: ForecastItem,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val date = sdf.format(Date(item.dt * 1000L))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            color = Color(0xFFBBDEFB),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        AsyncImage(
            model = Constants.ICON_URL.format(
                item.weather.firstOrNull()?.icon ?: "01d"
            ),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "↑ ${item.main.tempMax.roundToInt()}°",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "↓ ${item.main.tempMin.roundToInt()}°",
            color = Color(0xFF90CAF9),
            fontSize = 14.sp
        )
    }
}
