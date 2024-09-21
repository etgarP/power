package com.example.power.ui.configure

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * function to activate haptic feedback
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun performHapticFeedback(context: android.content.Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    if (vibrator?.hasVibrator() == true) {
        // Use a lower amplitude for a lighter vibration
        val amplitude = 40
        val effect = VibrationEffect.createOneShot(10, VibrationEffect.EFFECT_DOUBLE_CLICK)
        vibrator.vibrate(effect)
    }
}

/**
 * function to activate haptic feedback
 */
fun formatDate(date: Date): String {
    // Create a date formatter with the desired format
    val formatter = SimpleDateFormat("dd MMMM yyyy")

    // Format the date as a string
    return formatter.format(date)
}

/**
 * returns a randoom number
 */
fun getRandomNumber(input: String, range: Int = 6): Int {
    val hashCode = input.hashCode()
    val randomNumber = (hashCode % range)
    return if (randomNumber < 0) randomNumber + range else randomNumber // Ensure positive result
}
