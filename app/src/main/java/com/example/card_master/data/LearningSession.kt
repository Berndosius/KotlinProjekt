package com.example.card_master.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_history")
data class LearningSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(), // Wann wurde gelernt?
    val cardsLearned: Int, // Anzahl der Karten
    val successRate: Double // Erfolgsquote in Prozent
)