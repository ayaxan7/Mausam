package com.ayaan.mausam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ayaan.mausam.ui.screens.HistoryScreen
import com.ayaan.mausam.ui.screens.HomeScreen
import com.ayaan.mausam.ui.theme.MausamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MausamTheme {
                MausamNavGraph()
            }
        }
    }
}

@Composable
fun MausamNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable(Route.Home.path) {
            HomeScreen(onNavigateToHistory = { navController.navigate("history") })
        }
        composable(Route.History.path) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
sealed class Route(val path:String){
    object Home:Route("home")
    object History:Route("history")

}