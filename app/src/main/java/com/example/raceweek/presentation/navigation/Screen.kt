package com.example.raceweek.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    data object Detail : Screen("detail/{raceId}") {
        fun createRoute(raceId: String) = "detail/$raceId"
    }
    data object Settings : Screen("settings")
}
