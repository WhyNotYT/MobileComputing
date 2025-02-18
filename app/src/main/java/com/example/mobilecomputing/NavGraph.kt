package com.example.mobilecomputing.navigation

sealed class NavGraph(val route: String) {
    object Main : NavGraph("main")
    object Second : NavGraph("second")
    object Sensor : NavGraph("sensor")
}