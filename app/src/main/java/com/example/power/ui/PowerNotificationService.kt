package com.example.power.ui

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.power.R


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

//    fun showExpandableNotification(){
//        val notification=NotificationCompat.Builder(context,"water_notification")
//            .setContentTitle("Water Reminder")
//            .setContentText("Time to drink a glass of water")
//            .setSmallIcon(R.drawable.water_icon)
//            .setPriority(NotificationManager.IMPORTANCE_HIGH)
//            .setAutoCancel(true)
//            .setStyle(
//                NotificationCompat
//                    .BigPictureStyle()
//                    .bigPicture(
//                        context.bitmapFromResource(
//                            R.drawable.img_1
//                        )
//                    )
//            )
//            .build()
//             notificationManager.notify(Random.nextInt(),notification)
//    }

//    private fun Context.bitmapFromResource(
//        @DrawableRes resId:Int
//    )= BitmapFactory.decodeResource(
//        resources,
//        resId
//    )
}