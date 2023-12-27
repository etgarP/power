package com.example.power.data

import java.util.Timer
import java.util.TimerTask

class CountdownTimer(
    private var totalSeconds: Int,
) {
    private var currentSeconds: Int = totalSeconds
    private var timer: Timer? = null

    fun loadTime(seconds: Int) {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        totalSeconds = seconds
        currentSeconds = seconds
    }

    fun reset() {
        loadTime(totalSeconds)
    }

    fun start(onTick: (String) -> Unit, onFinish: () -> Unit) {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (currentSeconds <= 0) {
                    onFinish.invoke()
                    timer?.cancel()
                    onFinish()
                } else {
                    currentSeconds--
                    onTick(getFormattedTime())
                }
            }
        }, 0, 1000)
    }

    fun getFormattedTime(): String {
        val minutes = currentSeconds / 60
        val seconds = currentSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}