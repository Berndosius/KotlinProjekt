package com.example.card_master.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.card_master.ui.CardMasterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    viewModel: CardMasterViewModel,
    deckId: Long,
    deckName: String,
    onSessionFinished: () -> Unit // Wenn alle Karten gelernt wurden, geht es zurück zum Dashboard
) {
    // States für diesen Screen
    var dueCards by remember { mutableStateOf<List<com.example.card_master.data.Flashcard>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Statistiken für das Logbuch am Ende der Sitzung
    var totalCardsInSession by remember { mutableStateOf(0) }
    var correctAnswersCount by remember { mutableStateOf(0) }

    // Daten beim Start laden
    // LaunchedEffect führt Code genau einmal aus, wenn der Bildschirm geöffnet wird
    LaunchedEffect(deckId) {
        dueCards = viewModel.getDueCards(deckId)
        totalCardsInSession = dueCards.size
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lernen: $deckName") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator() // Lade-Kringel anzeigen
            } else if (dueCards.isEmpty() || currentIndex >= dueCards.size) {
                // SITZUNG BEENDET
                // Speichere die Statistik in der Datenbank, bevor wir den Bildschirm verlassen
                LaunchedEffect(Unit) {
                    if (totalCardsInSession > 0) {
                        val rate = (correctAnswersCount.toDouble() / totalCardsInSession.toDouble()) * 100
                        viewModel.saveLearningSession(totalCardsInSession, rate)
                    }
                    onSessionFinished()
                }
            } else {
                // Aktuelle Karte anzeigen
                val currentCard = dueCards[currentIndex]

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Fortschrittsanzeige
                    Text(
                        text = "Karte ${currentIndex + 1} von $totalCardsInSession",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Die Karte selbst
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clickable { isCardFlipped = !isCardFlipped }, // Wechselt Zustand bei Klick
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCardFlipped) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isCardFlipped) currentCard.back else currentCard.front,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Text(
                        text = "Tippe auf die Karte, um sie umzudrehen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons zur Bewertung der Karte
                    if (isCardFlipped) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Button: Nicht gewusst (Falsch)
                            Button(
                                onClick = {
                                    viewModel.updateCardAfterReview(currentCard, wasCorrect = false)
                                    isCardFlipped = false // Für die nächste Karte zurücksetzen
                                    currentIndex++        // Zur nächsten Karte springen
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("❌ Falsch")
                            }

                            // Button: Gewusst (Richtig)
                            Button(
                                onClick = {
                                    viewModel.updateCardAfterReview(currentCard, wasCorrect = true)
                                    correctAnswersCount++
                                    isCardFlipped = false
                                    currentIndex++
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("✅ Richtig")
                            }
                        }
                    }
                }
            }
        }
    }
}