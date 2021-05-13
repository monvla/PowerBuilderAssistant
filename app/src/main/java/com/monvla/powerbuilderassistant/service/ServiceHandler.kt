package com.monvla.powerbuilderassistant.service

import android.os.Handler
import android.os.Message
import timber.log.Timber
import java.lang.ref.WeakReference

class ServiceHandler(val service: WeakReference<TrainingService>) : Handler() {

    private val timerRunnable = object : Runnable {
        override fun run() {
            val service = service.get()
            service?.let {
                if (it.isRunning()) {
                    it.updateNotificationTime()
                    postDelayed(this, 50);
                }
            }
        }
    }

    override fun handleMessage(msg: Message) {
        Timber.d("HANDLE MESSAGE: msg ${msg.arg1} ${msg.arg2}")
        postDelayed(timerRunnable, 0);
    }
}