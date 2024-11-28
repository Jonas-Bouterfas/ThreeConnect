package com.example.threeconnect.model

data class Player(
    var playerId: String = "",    // Unique player ID
    var name: String = "",        // Player's name
    var score: Int = 0,           // Player's score
    var invitation: String = ""   // Invitation status message
)
