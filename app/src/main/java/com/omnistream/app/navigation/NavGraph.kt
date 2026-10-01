package com.omnistream.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omnistream.app.feature.home.HomeScreen
import com.omnistream.app.feature.player.PlayerScreen

object Routes {
    const val HOME = "home"
    const val PLAYER = "player/{mediaId}"
}

@Composable
fun OmniStreamNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(onNavigateToPlayer = { mediaId ->
                navController.navigate("player/$mediaId")
            })
        }
        composable(Routes.PLAYER) {
            PlayerScreen(onBackClick = { navController.popBackStack() })
        }
    }
}