package com.example.card_master

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.card_master.boot.BootReceiver
import com.example.card_master.ui.CardMasterViewModel
import com.example.card_master.ui.screen.DashboardScreen
import com.example.card_master.ui.screen.DeckDetailsScreen
import com.example.card_master.ui.screen.HistoryScreen
import com.example.card_master.ui.screen.SessionScreen

class MainActivity : ComponentActivity() {

    // Das ViewModel wird über die Android-Klassen bereitgestellt
    private val viewModel: CardMasterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Aktiviert tägliche Erinnerung beim App-Start
        BootReceiver.scheduleDailyReminder(this)

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // --- Navigation über State ---
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

                    when (val screen = currentScreen) {
                        is Screen.Dashboard -> {
                            DashboardScreen(
                                viewModel = viewModel,
                                onDeckClick = { id, name -> currentScreen = Screen.DeckDetails(id, name) },
                                onViewHistoryClick = { currentScreen = Screen.History }
                            )
                        }
                        is Screen.DeckDetails -> {
                            DeckDetailsScreen(
                                viewModel = viewModel,
                                deckId = screen.id,
                                deckName = screen.name,
                                onStartLearningClick = { currentScreen = Screen.LearningSession(screen.id, screen.name) },
                                onBackClick = { currentScreen = Screen.Dashboard }
                            )
                        }
                        is Screen.LearningSession -> {
                            SessionScreen(
                                viewModel = viewModel,
                                deckId = screen.id,
                                deckName = screen.name,
                                onSessionFinished = { currentScreen = Screen.Dashboard }
                            )
                        }
                        is Screen.History -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onBackClick = { currentScreen = Screen.Dashboard }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Eine versiegelte Klasse, welche die verfügbaren Bildschirme definiert
sealed class Screen {
    object Dashboard : Screen()
    object History : Screen()
    data class DeckDetails(val id: Long, val name: String) : Screen()
    data class LearningSession(val id: Long, val name: String) : Screen()
}