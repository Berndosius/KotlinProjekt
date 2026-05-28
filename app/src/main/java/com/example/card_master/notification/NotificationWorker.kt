package com.example.card_master.notification

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "cardmaster_reminders"

        // 1. Benachrichtigungskanal erstellen (Ab Android 8.0 Pflicht)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lern-Erinnerungen",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Erinnerungen für fällige Karteikarten"

                // Verhindert, dass sensible Inhalte auf dem Sperrbildschirm landen
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Die Benachrichtigung bauen
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm) // Nutzt ein Standard-Android-Icon
            .setContentTitle("CardMaster")
            // Sichtbarer Text auf dem Sperrbildschirm bei VISIBILITY_PRIVATE:
            .setTicker("Zeit zum Lernen!")
            .setContentText("Zeit zum Lernen! Deine Karteikarten warten auf dich.")
            // Erhöht die Privatsphäre zusätzlich für ältere/bestimmte Android-Systeme
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .build()

        // 3. Absenden
        notificationManager.notify(1001, notification)

        return Result.success()
    }
}