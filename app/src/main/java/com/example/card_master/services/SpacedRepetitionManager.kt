package com.example.card_master.services

import com.example.card_master.data.Flashcard
import java.util.concurrent.TimeUnit

// Singleton Objekt
object SpacedRepetitionManager {
    /**
     * Berechnet die aktualisierte Karteikarte basierend darauf,
     * ob der Nutzer die Antwort gewusst hat oder nicht.
     */

    fun calculateNextReview(card: Flashcard, wasCorrect: Boolean): Flashcard {
        val newStage = if (wasCorrect) {
            // Wenn richtig: Erhöhe die Stufe um 1, aber maximal bis Stufe 3
            (card.repetitionStage + 1).coerceAtMost(3)
        } else {
            // Wenn falsch: Zurück auf Stufe 0
            0
        }

        // Bestimme den Zeitabstand in Millisekunden basierend auf der neuen Stufe
        val delayMillis = when (newStage) {
            1 -> TimeUnit.MINUTES.toMillis(1)   // 1 Minute
            2 -> TimeUnit.MINUTES.toMillis(5)   // 5 Minuten
            3 -> TimeUnit.DAYS.toMillis(1)      // 24 Stunden
            else -> 0L                          // Stufe 0: Sofort wieder fällig
        }

        val currentTime = System.currentTimeMillis()
        val newNextReviewTimestamp = currentTime + delayMillis

        // Gib eine Kopie der Karte mit den neuen Werten zurück
        return card.copy(
            repetitionStage = newStage,
            nextReviewTimestamp = newNextReviewTimestamp
        )
    }
}