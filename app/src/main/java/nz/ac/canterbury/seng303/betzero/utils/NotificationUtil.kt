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
            val notification = NotificationCompat.Builder(context, "water_reminder")
                .setContentTitle("Water Reminder")
                .setContentText("Time to drink some water!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(
                Random.nextInt(),
                notification
            )
        }

        fun showNotificationGroup() {
            val groupId = "water_group"
            val summaryId = 0

            val notification1 = NotificationCompat.Builder(context, "water_reminder")
                .setContentTitle("Water Reminder")
                .setContentText("Time to drink some water!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat
                        .InboxStyle()
                        .addLine("Line 1")
                )
                .setAutoCancel(true)
                .setGroup(groupId)
                .build()

            val notification2 = NotificationCompat.Builder(context, "daily_log_reminder")
                .setContentTitle("daily log reminder")
                .setContentText("Time to drink some water!")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat
                        .InboxStyle()
                        .addLine("Line 1")
                        .addLine("Line 2")
                )
                .setAutoCancel(true)
                .setGroup(groupId)
                .build()

            notificationManager.notify(
                Random.nextInt(),
                notification1
            )
            notificationManager.notify(
                Random.nextInt(),
                notification2
            )
        }

        private fun Context.bitmapFromResource(
            @DrawableRes resId: Int
        ) = BitmapFactory.decodeResource(
            resources,
            resId
        )
}