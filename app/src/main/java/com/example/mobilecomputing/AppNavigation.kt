package com.example.mobilecomputing.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilecomputing.screens.MainScreen
import com.example.mobilecomputing.screens.SecondScreen
import com.example.mobilecomputing.screens.SensorScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavGraph.Main.route
    ) {
        composable(NavGraph.Main.route) {
            MainScreen(
                onNavigateToSecond = {
                    navController.navigate(NavGraph.Second.route) {
                        launchSingleTop = true
                        popUpTo(NavGraph.Main.route) {
                            saveState = true
                        }
                    }
                },
                onNavigateToSensor = {
                    navController.navigate(NavGraph.Sensor.route) {
                        launchSingleTop = true
                        popUpTo(NavGraph.Main.route) {
                            saveState = true
                        }
                    }
                }
            )
        }
        composable(NavGraph.Second.route) {
            SecondScreen(
                onNavigateBack = {
                    navController.navigate(NavGraph.Main.route) {
                        popUpTo(NavGraph.Main.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        composable(NavGraph.Sensor.route) {
            SensorScreen(
                onNavigateBack = {
                    navController.navigate(NavGraph.Main.route) {
                        popUpTo(NavGraph.Main.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}