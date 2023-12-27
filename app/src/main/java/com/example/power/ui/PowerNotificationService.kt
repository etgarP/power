package com.example.power.ui

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.power.R
import kotlin.random.Random

class PowerNotificationService(
    private val context: Context
){
    private val notificationManager=context.getSystemService(NotificationManager::class.java)
    fun showBasicNotification(){
        val notification= NotificationCompat.Builder(context,"power_notification")
            .setContentTitle("Workout Break Over")
            .setContentText("Time to continue your workout.")
            .setSmallIcon(R.drawable.power_icon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
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