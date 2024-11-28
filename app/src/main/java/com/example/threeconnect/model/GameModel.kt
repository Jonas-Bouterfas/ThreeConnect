package com.example.threeconnect.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.threeconnect.ui.Player
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow

data class Player(
    var name: String = ""
)
data class Game(
    var gameBoard: List<Int> = List(9) { 0 },
    var gameState: String = "invite",
    var player1Id: String = "",
    var player2Id: String = ""
)

const val rows = 3
const val cols = 3

class GameModel: ViewModel() {
    val db = Firebase.firestore
    var localPlayerId = mutableStateOf<String?>(null)
    val playerMap = MutableStateFlow<Map<String, Player>>(emptyMap())
    val gameMap = MutableStateFlow<Map<String, Game>>(emptyMap())

    fun initGame(){
        db.collection("players")
            .addSnapshotListener{value, error ->
                if(error != null){
                    return@addSnapshotListener
                }
                if(vale != null){
                    val updatedMap = value.documents.associate { doc ->
                        doc.id
                    }
                }

            }
    }

}