package com.example.card_master.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Deck::class, Flashcard::class, LearningSession::class], version = 1, exportSchema = false) // Anmelden der Tabellen
abstract class AppDatabase : RoomDatabase() {

    // Registrieren des DAO
    abstract fun cardMasterDao(): CardMasterDao

    // Singleton Instanz der Datenbank
    companion object {
        @Volatile // Der Wert von INSTANCE ist für alle Threads gleichermaßen sichtbar
        private var INSTANCE: AppDatabase? = null

        // Instanz holen
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // Sperrt den Block für andere Threads. Nur einer kann bauen
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cardmaster_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}