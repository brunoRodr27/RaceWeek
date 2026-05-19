package com.example.raceweek.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.raceweek.presentation.agenda.AgendaViewModel
import com.example.raceweek.presentation.detail.DetailRoute
import com.example.raceweek.presentation.main.MainRoute
import com.example.raceweek.presentation.settings.SettingsRoute
import com.example.raceweek.presentation.splash.SplashRoute

private const val ANIM_DURATION = 300
private const val ROUTE_MAIN_FLOW = "main_flow"

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { slideIn(tween(ANIM_DURATION)) { IntOffset(it.width, 0) } + fadeIn(tween(ANIM_DURATION)) },
        exitTransition = { slideOut(tween(ANIM_DURATION)) { IntOffset(-it.width, 0) } + fadeOut(tween(ANIM_DURATION)) },
        popEnterTransition = { slideIn(tween(ANIM_DURATION)) { IntOffset(-it.width, 0) } + fadeIn(tween(ANIM_DURATION)) },
        popExitTransition = { slideOut(tween(ANIM_DURATION)) { IntOffset(it.width, 0) } + fadeOut(tween(ANIM_DURATION)) }
    ) {
        composable(
            route = Screen.Splash.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            SplashRoute(
                onSplashFinished = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Main e Detail compartilham o mesmo AgendaViewModel, escopado a este grafo aninhado.
        navigation(startDestination = Screen.Main.route, route = ROUTE_MAIN_FLOW) {
            composable(
                route = Screen.Main.route,
                enterTransition = { EnterTransition.None },
                exitTransition = { slideOut(tween(ANIM_DURATION)) { IntOffset(-it.width, 0) } + fadeOut(tween(ANIM_DURATION)) },
                popEnterTransition = { slideIn(tween(ANIM_DURATION)) { IntOffset(-it.width, 0) } + fadeIn(tween(ANIM_DURATION)) },
                popExitTransition = { ExitTransition.None }
            ) { backStack ->
                val parentEntry = remember(backStack) { navController.getBackStackEntry(ROUTE_MAIN_FLOW) }
                val viewModel: AgendaViewModel = hiltViewModel(parentEntry)
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
                val parentEntry = remember(backStack) { navController.getBackStackEntry(ROUTE_MAIN_FLOW) }
                val viewModel: AgendaViewModel = hiltViewModel(parentEntry)
                val raceId = backStack.arguments?.getString("raceId") ?: ""
                DetailRoute(
                    raceId = raceId,
                    agendaViewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(route = Screen.Settings.route) {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
    }
}
