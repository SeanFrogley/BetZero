package nz.ac.canterbury.seng303.betzero.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import nz.ac.canterbury.seng303.betzero.R
import kotlin.random.Random

class AlarmUtil : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val notificationUtil = NotificationUtil(context)
            notificationUtil.showBasicNotification()
        } catch (ex: Exception) {
            Log.d("Receive Ex", "onReceive: ${ex.printStackTrace()}")
        }
    }
}

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

        try {
            notificationManager.notify(
                Random.nextInt(),
                notification
            )
        } catch (ex: Exception) {
            Log.d("NotificationUtil", "Error showing notification", ex)
        }

    }
}