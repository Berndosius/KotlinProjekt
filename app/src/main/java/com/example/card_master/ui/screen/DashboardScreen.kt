package com.example.card_master.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.card_master.ui.CardMasterViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Zeichnet Dashboard Screen
fun DashboardScreen(
    viewModel: CardMasterViewModel,
    onDeckClick: (Long, String) -> Unit, // Navigation zum Lernmodus/Karten-Ansicht
    onViewHistoryClick: () -> Unit // Navigation zur History
) {
    val context = LocalContext.current

    // Beobachte den Flow aus dem ViewModel. Compose zeichnet sich bei Änderungen neu.
    val decks by viewModel.allDecks.collectAsState(initial = emptyList()) // Alle Decks
    var showDialog by remember { mutableStateOf(false) } // Startwert false und Compose merkt sich den Zustand bei jeder Änderung.
    var newDeckName by remember { mutableStateOf("") } // Name des neuen Stapels

    Scaffold( // Standardisierte Layout-Struktur
        topBar = {
            TopAppBar(
                title = { Text("CardMaster – Meine Stapel") },
                actions = {
                    // Debug Test Button für Notification. Für den Notfall
                    IconButton(onClick = {
                        val testRequest = OneTimeWorkRequestBuilder<com.example.card_master.notification.NotificationWorker>()
                            .setInitialDelay(5, TimeUnit.SECONDS)
                            .build()
                        WorkManager.getInstance(context).enqueue(testRequest)
                    }) {
                        Text("🔔")
                    }

                    IconButton(onClick = onViewHistoryClick) {
                        Text("📊") // Simples Icon für die History
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { paddingValues ->
        // Der eigentliche Inhalt der Layout-Struktur
        Column( // Column: Ordnet Elemente untereinander an
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (decks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { // Box: zentriert hier das Element im Raum
                    Text("Noch keine Kartenstapel vorhanden. Lege einen an!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) { // LazyColumn: Scrollbar. Äquivalent zu RecyclerView. Rendert nur die sichtbaren Elemente.
                    items(decks) { deck ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onDeckClick(deck.id, deck.name) }
                        ) {
                            Row( // Row: Ordnet Elemente horizontal an
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(deck.name, style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Mülleimer-Button zum Löschen des Decks
                                    IconButton(
                                        onClick = { viewModel.deleteDeckAndItsCards(deck) }
                                    ) {
                                        Text(
                                            "🗑️",
                                            style = MaterialTheme.typography.titleMedium
                                        ) // Simples Mülleimer-Icon
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("▶", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                    }
                }
            }

            // Dialog zum Anlegen eines neuen Stapels
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Neuen Stapel anlegen") },
                    text = {
                        OutlinedTextField(
                            value = newDeckName,
                            onValueChange = { newDeckName = it },
                            label = { Text("Name des Stapels") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newDeckName.isNotBlank()) {
                                    viewModel.addDeck(newDeckName)
                                    newDeckName = ""
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Hinzufügen")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Abbrechen")
                        }
                    }
                )
            }
        }
    }
}