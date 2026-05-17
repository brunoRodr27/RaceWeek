package com.example.raceweek.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.raceweek.presentation.agenda.AgendaViewModel
import com.example.raceweek.presentation.detail.DetailRoute
import com.example.raceweek.presentation.main.MainRoute
import com.example.raceweek.presentation.settings.SettingsRoute
import com.example.raceweek.presentation.splash.SplashRoute

@Composable
fun NavGraph(navController: NavHostController) {
    // Instanciado aqui, fora de qualquer composable{}, o ViewModel é criado no momento
    // em que o NavGraph é composto — ainda durante a splash screen. O init block
    // dispara imediatamente, dando aos carregamentos do Firestore ~1400ms de vantagem.
    val viewModel: AgendaViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashRoute(
                onSplashFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainRoute(
                viewModel = viewModel,
                onNavigateToDetail = { raceId ->
                    navController.navigate(Screen.Detail.createRoute(raceId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("raceId") { type = NavType.StringType })
        ) { backStack ->
            val raceId = backStack.arguments?.getString("raceId") ?: ""
            DetailRoute(
                raceId = raceId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
    }
}
