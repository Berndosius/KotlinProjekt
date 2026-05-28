package com.example.card_master.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.card_master.data.AppDatabase
import com.example.card_master.data.Deck
import com.example.card_master.data.Flashcard
import com.example.card_master.data.LearningSession
import com.example.card_master.services.SpacedRepetitionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CardMasterViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).cardMasterDao()

    // Datenströme (Flows) für die UI – aktualisieren sich automatisch bei Änderungen
    val allDecks: Flow<List<Deck>> = dao.getAllDecks()
    val history: Flow<List<LearningSession>> = dao.getHistory()

    // --- DECKS ---
    fun addDeck(name: String) {
        viewModelScope.launch {
            dao.insertDeck(Deck(name = name))
        }
    }

    // --- CARDS ---
    fun addCard(deckId: Long, front: String, back: String) {
        viewModelScope.launch {
            dao.insertCard(Flashcard(deckId = deckId, front = front, back = back))
        }
    }

    fun getCardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return dao.getCardsForDeck(deckId)
    }

    // Holt fällige Karten asynchron für den Lernmodus
    suspend fun getDueCards(deckId: Long): List<Flashcard> {
        return dao.getDueCardsForDeck(deckId, System.currentTimeMillis())
    }

    fun updateCardAfterReview(card: Flashcard, wasCorrect: Boolean) {
        viewModelScope.launch {
            // Hier nutzen wir unseren Algorithmus aus Phase 3!
            val updatedCard = SpacedRepetitionManager.calculateNextReview(card, wasCorrect)
            dao.updateCard(updatedCard)
        }
    }

    // --- HISTORY ---
    fun saveLearningSession(cardsLearned: Int, successRate: Double) {
        viewModelScope.launch {
            dao.insertSession(
                LearningSession(cardsLearned = cardsLearned, successRate = successRate)
            )
        }
    }

    // Einzelne Karte löschen
    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            dao.deleteCards(listOf(card))
        }
    }

    // Einen ganzen Stapel UND alle seine Karten löschen
    fun deleteDeckAndItsCards(deck: Deck) {
        viewModelScope.launch {
            // 1. Hole alle Karten des Decks
            // (Wir nutzen einen direkten Aufruf, da Flow hier zu träge wäre)
            val cardsForDeck = dao.getCardsForDeckSync(deck.id)
            // 2. Lösche all diese Karten
            dao.deleteCards(cardsForDeck)
            // 3. Lösche das Deck selbst
            dao.deleteDeck(deck)
        }
    }
}