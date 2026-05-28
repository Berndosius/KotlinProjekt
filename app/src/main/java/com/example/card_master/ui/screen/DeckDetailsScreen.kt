package com.example.card_master.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.card_master.ui.CardMasterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    viewModel: CardMasterViewModel,
    deckId: Long,
    deckName: String,
    onStartLearningClick: () -> Unit, // Navigation zum Lernmodus
    onBackClick: () -> Unit // Zurück zum Dashboard
) {
    // Date Formatter für Debugging weiter unten
    val dateFormatter = remember {
        java.text.SimpleDateFormat("dd.MM.yy HH:mm:ss", java.util.Locale.getDefault())
    }

    // States.
    // Es werden alle Karten beobachtet die zu dem Stapel gehören
    val cards by viewModel.getCardsForDeck(deckId).collectAsState(initial = emptyList())

    var frontText by remember { mutableStateOf("") }
    var backText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deckName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("◀") // Simpler Zurück-Pfeil
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Neue Karte hinzufügen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Neue Karteikarte erstellen", style = MaterialTheme.typography.titleSmall)

                    OutlinedTextField(
                        value = frontText,
                        onValueChange = { frontText = it },
                        label = { Text("Vorderseite (Frage)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = backText,
                        onValueChange = { backText = it },
                        label = { Text("Rückseite (Antwort)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (frontText.isNotBlank() && backText.isNotBlank()) {
                                viewModel.addCard(deckId, frontText, backText)
                                // Felder nach dem Speichern leeren
                                frontText = ""
                                backText = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Karte speichern")
                    }
                }
            }

            // Lernmodus starten (Nur aktiv, wenn Karten vorhanden sind)
            Button(
                onClick = onStartLearningClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = cards.isNotEmpty() // Deaktiviert den Button, wenn die Liste leer ist
            ) {
                Text("Jetzt Lernen (Fällige Karten abfragen)")
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Text(
                text = "Karten in diesem Stapel (${cards.size})",
                style = MaterialTheme.typography.titleMedium
            )

            // Bereits vorhandene Karten anzeigen
            if (cards.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Dieser Stapel ist noch leer.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(cards) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Vorderseite:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    Text(card.front, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Rückseite:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    Text(card.back, style = MaterialTheme.typography.bodyMedium)

                                    // Debugging Information für den Algorithmus. Stufe und Fälligkeitsdatum sind sichtbar gemacht.
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        Modifier,
                                        DividerDefaults.Thickness,
                                        DividerDefaults.color
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val dateString = dateFormatter.format(java.util.Date(card.nextReviewTimestamp))
                                    Text(
                                        text = "Algorithmus: Stufe ${card.repetitionStage} | Fällig ab: $dateString",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Mülleimer-Button für die einzelne Karte
                                IconButton(
                                    onClick = { viewModel.deleteCard(card) }
                                ) {
                                    Text("🗑️")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}