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

        // Benachrichtigungskanal erstellen
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

        // Die öffentliche Version. Sieht man auf Sperrbildschirm.
        val publicNotification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle("CardMaster")
            .setContentText("Zeit zum Lernen!") // Der neutrale Text
            .build()

        // Die private Version
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle("CardMaster")
            .setContentText("Deine Karteikarten warten auf dich. Leg los!") // Der detaillierte Text
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicNotification) // Die öffentliche Variante
            .setAutoCancel(true)
            .build()

        // Absenden
        notificationManager.notify(1001, notification)

        return Result.success()
    }
}