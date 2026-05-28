package com.example.card_master.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.card_master.notification.NotificationWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Reagiert auf das Hochfahren des Geräts
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleDailyReminder(context)
        }
    }

    companion object {
        // Tägliche Erinnerung
        fun scheduleDailyReminder(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                20, TimeUnit.SECONDS // Alle 24 Stunden erinnern
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "cardmaster_daily_reminder",
                ExistingPeriodicWorkPolicy.KEEP, // Behält den bestehenden Timer, falls er schon läuft
                workRequest
            )
        }
    }
}