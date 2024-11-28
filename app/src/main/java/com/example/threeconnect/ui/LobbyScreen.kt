package com.example.threeconnect.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.threeconnect.model.Game
import com.example.threeconnect.model.GameModel
import kotlinx.coroutines.flow.asStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(navController: NavController, model: GameModel) {
    val players by model.playerMap.asStateFlow().collectAsStateWithLifecycle()
    val games by model.gameMap.asStateFlow().collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf<String?>(null) } // Manage dialog visibility
    var selectedGameId by remember { mutableStateOf<String?>(null) } // Store selected game ID

    LaunchedEffect(games) {
        games.forEach { (gameId, game) ->
            if (game.gameState == "invite" && selectedGameId == null) {
                // Show the accept/decline popup for an invite
                showDialog = gameId
                selectedGameId = gameId
            } else if ((game.player1Id == model.localPlayerId.value || game.player2Id == model.localPlayerId.value)
                && (game.gameState == "player1_turn" || game.gameState == "player2_turn")) {
                // If the game is in progress, navigate directly to the game screen
                navController.navigate("game/$gameId")
            }
        }
    }

    var playerName = "Unknown?"
    players[model.localPlayerId.value]?.let {
        playerName = it.name
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("TicTacToe - $playerName") }) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(players.entries.toList()) { (documentId, player) ->
                if (documentId != model.localPlayerId.value) { // Don't show yourself
                    ListItem(
                        headlineContent = {
                            Text("Player Name: ${player.name}")
                        },
                        supportingContent = {
                            Text("Status: ...")
                        },
                        trailingContent = {
                            var hasGame = false
                            games.forEach { (gameId, game) ->
                                if (game.player1Id == model.localPlayerId.value
                                    && game.gameState == "invite") {
                                    Text("Waiting for accept...")
                                    hasGame = true
                                } else if (game.player2Id == model.localPlayerId.value
                                    && game.gameState == "invite") {
                                    Button(onClick = {
                                        model.db.collection("games").document(gameId)
                                            .update("gameState", "player1_turn")
                                            .addOnSuccessListener {
                                                navController.navigate("game/${gameId}")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Error", "Error updating game: $gameId")
                                            }
                                    }) {
                                        Text("Accept invite")
                                    }
                                    hasGame = true
                                }
                            }
                            if (!hasGame) {
                                Button(onClick = {
                                    model.db.collection("games")
                                        .add(
                                            Game(gameState = "invite",
                                                player1Id = model.localPlayerId.value!!,
                                                player2Id = documentId)
                                        )
                                        .addOnSuccessListener { documentRef ->
                                            // Navigate to the game screen after a successful game creation
                                            navController.navigate("game/${documentRef.id}")
                                        }
                                }) {
                                    Text("Challenge")
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Show the Accept Invite Dialog if showDialog is not null (meaning there's an invite to accept)
    if (showDialog != null) {
        ShowAcceptInviteDialog(
            gameId = showDialog!!,
            onAccept = {
                // Accept the game invite, change the game state to "player1_turn"
                model.db.collection("games").document(showDialog!!)
                    .update("gameState", "player1_turn")
                    .addOnSuccessListener {
                        navController.navigate("game/${showDialog!!}")
                    }
                    .addOnFailureListener {
                        Log.e("Error", "Error accepting invite: ${showDialog!!}")
                    }
            },
            onDecline = {
                // Decline the invitation, change the game state to "declined"
                model.db.collection("games").document(showDialog!!)
                    .update("gameState", "declined")
                    .addOnFailureListener {
                        Log.e("Error", "Error declining game: ${showDialog!!}")
                    }
            }
        )
    }
}

@Composable
fun ShowAcceptInviteDialog(
    gameId: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onDecline() }, // Dismiss the dialog if the user taps outside
        title = {
            Text(text = "Game Invitation")
        },
        text = {
            Text("Do you want to accept the game invite?")
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Accept")
            }
        },
        dismissButton = {
            Button(onClick = onDecline) {
                Text("Decline")
            }
        }
    )
}
