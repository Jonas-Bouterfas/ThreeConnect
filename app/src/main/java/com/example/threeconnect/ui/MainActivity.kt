package com.example.threeconnect.ui



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.toObjects
import com.example.threeconnect.model.Player
import com.example.threeconnect.theme.ThreeConnectTheme
import com.google.firebase.FirebaseApp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            ThreeConnectTheme {
                MainScreen()

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val db = Firebase.firestore
    val playerList = MutableStateFlow<List<Player>>(emptyList())

    db.collection("Players")
        .addSnapshotListener{ value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (value != null) {
                playerList.value = value.toObjects()
            }
        }

    val players by playerList.asStateFlow().collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(players) { player ->

                ListItem(
                    overlineContent = {
                        Text("Invitation Status: ${player.invitation}")

                    },
                    headlineContent = {
                        Text("Name: ${player.name}")
                    },
                    supportingContent  = {
                        Text("Score: ${player.score}")
                    },
                    trailingContent = {
                        Button(onClick = {
                            val query = db.collection("Players").whereEqualTo("playerId", player.playerId)

                            query.get().addOnSuccessListener { querySnapshot ->
                                for (documentSnapshot in querySnapshot) {
                                    documentSnapshot.reference.update("invitation", "Hello!")
                                }}
                        }) {
                            Text("Invite")
                        }
                    }
                )


            }
        }
    }
}




