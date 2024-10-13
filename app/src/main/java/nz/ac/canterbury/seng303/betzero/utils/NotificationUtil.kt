package nz.ac.canterbury.seng303.betzero.utils

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import nz.ac.canterbury.seng303.betzero.R
import kotlin.random.Random

class NotificationUtil (
    private val context: Context
    ) {
        private val notificationManager = context.getSystemService(NotificationManager::class.java)

        fun showBasicNotification() {
            val notification = NotificationCompat.Builder(context, "daily_log_reminder") //haven't removed this channelid
                .setContentTitle("Daily Log Reminder")
                .setContentText("Have you completed your daily log?")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(
                Random.nextInt(),
                notification
            )
        }
}