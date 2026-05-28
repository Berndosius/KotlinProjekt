package com.example.card_master.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long, // Die Verbindung zum Kartenstapel
    val front: String, // Vorderseite
    val back: String,  // Rückseite
    val nextReviewTimestamp: Long = System.currentTimeMillis(), // Wann steht die Karte an?
    val repetitionStage: Int = 0 // Für den Spaced-Repetition Algorithmus
)