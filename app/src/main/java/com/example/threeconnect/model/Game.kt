package com.example.threeconnect.model

data class Game(
    var gameBoard: List<Int> = List(9) { 0 },
    var gameState: String = "invite",
    var player1Id: String = "",
    var player2Id: String = ""
)