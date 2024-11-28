package com.example.threeconnect.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.threeconnect.model.GameModel
import com.example.threeconnect.ui.GameScreen
import com.example.threeconnect.ui.LobbyScreen
import com.example.threeconnect.ui.NewPlayerScreen

@Composable
fun TicTacToe2() {
    val navController = rememberNavController()
    val model = GameModel()
    model.initGame()

    NavHost(navController = navController, startDestination = "player") {
        composable("player") { NewPlayerScreen(navController, model) }
        composable("lobby") { LobbyScreen(navController, model) }
        composable("game/{gameId}") { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId")
            GameScreen(navController, model, gameId)
        }
    }
}