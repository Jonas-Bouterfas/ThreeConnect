package com.example.threeconnect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class Player( var name: String = "")

data class Game(
    var gameBoard: List<Int> = List(9) { 0 },
    var gameState: String = "invite",
    var player1Id: String = "",
    var player2Id:
)