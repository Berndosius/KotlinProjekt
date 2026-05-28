package com.example.card_master.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardMasterDao {

    // --- DECKS ---
    @Insert
    suspend fun insertDeck(deck: Deck): Long

    @Query("SELECT * FROM decks")
    fun getAllDecks(): Flow<List<Deck>>

    // --- DECKS LÖSCHEN ---
    @Delete
    suspend fun deleteDeck(deck: Deck)

    // Löscht alle Karten, die zu einer bestimmten deckId gehören
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    suspend fun getCardsForDeckSync(deckId: Long): List<Flashcard> // Hilfsfunktion

    @Delete
    suspend fun deleteCards(cards: List<Flashcard>)

    // --- FLASHCARDS ---
    @Insert
    suspend fun insertCard(card: Flashcard)

    @Update
    suspend fun updateCard(card: Flashcard)

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    fun getCardsForDeck(deckId: Long): Flow<List<Flashcard>>

    // Holt alle Karten eines Decks, bei denen die Wiederholungszeit erreicht oder überschritten ist
    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewTimestamp <= :currentTimestamp")
    suspend fun getDueCardsForDeck(deckId: Long, currentTimestamp: Long): List<Flashcard>

    // --- HISTORY ---
    @Insert
    suspend fun insertSession(session: LearningSession)

    @Query("SELECT * FROM learning_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<LearningSession>>
}