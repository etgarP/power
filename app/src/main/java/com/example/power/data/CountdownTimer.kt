package com.example.power.data

import java.util.Timer
import java.util.TimerTask

/**
 * loads time stores time and returns formulated time
 */
class CountdownTimer(
    private var totalSeconds: Int // total number of seconds the timer will go for
) {
    /**
     * the current second
     */
    private var currentSeconds: Int = totalSeconds
    private var timer: Timer? = null
    private var onGoing = true

    /**
     * loads time to the timer
     */
    fun loadTime(seconds: Int) {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        totalSeconds = seconds
        currentSeconds = seconds
    }

    /**
     * resets the timer
     */
    fun reset() {
        loadTime(totalSeconds)
        timer?.cancel()
        onGoing = false
    }

    /**
     * starts the timer
     */
    fun start(onTick: (String) -> Unit, onFinish: () -> Unit) {
        onGoing = true
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (currentSeconds <= 0) {
                    if (onGoing) onFinish.invoke()
                    timer?.cancel()
                } else {
                    currentSeconds--
                    onTick(getFormattedTime())
                }
            }
        }, 0, 1000)
    }

    /**
     * returns the time formatted as a string
     */
    fun getFormattedTime(): String {
        val minutes = currentSeconds / 60
        val seconds = currentSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}