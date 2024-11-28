package com.example.threeconnect.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.threeconnect.model.GameModel
import com.example.threeconnect.model.Player

@Composable
fun NewPlayerScreen(navController: NavController, model: GameModel) {
    val sharedPreferences = LocalContext.current
        .getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE)

    // Check for playerId in SharedPreferences
    LaunchedEffect(Unit) {
        model.localPlayerId.value = sharedPreferences.getString("playerId", null)
        if (model.localPlayerId.value != null) {
            navController.navigate("lobby")
        }
    }

    if (model.localPlayerId.value == null) {

        var playerName by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Robins TicTacToe!")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (playerName.isNotBlank()) {
                    // Create new player in Firestore
                    val newPlayer = Player(name = playerName)

                    model.db.collection("players")
                        .add(newPlayer)
                        .addOnSuccessListener { documentRef ->
                            val newPlayerId = documentRef.id

                            // Save playerId in SharedPreferences
                            sharedPreferences.edit().putString("playerId", newPlayerId).apply()

                            // Update local variable and navigate to lobby
                            model.localPlayerId.value = newPlayerId
                            navController.navigate("lobby")
                        }.addOnFailureListener { error ->
                            Log.e("RobinError", "Error creating player: ${error.message}")
                        }
                } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Player")
            }
        }
    } else {
        Text("Laddar....")
    }
}
