package com.example.power.ui

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.power.R

/**
 * sends notification when a break is over
 * and can cancel notifications
 */
class PowerNotificationService(
    private val context: Context
){
    private val notificationManager=context.getSystemService(NotificationManager::class.java)
    private val NOTIFICATION_ID = 1
    fun showBasicNotification(
        title : String = "Workout Break Over",
        content : String = "Time to continue your workout."
    ){
        val notification= NotificationCompat.Builder(context,"power_notification")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.power_icon)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
    }
    fun cancelAll() {
        notificationManager!!.cancel(NOTIFICATION_ID)
    }
}