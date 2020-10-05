package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import androidx.core.app.NotificationCompat
import com.monvla.powerbuilderassistant.MainActivity
import com.monvla.powerbuilderassistant.MainActivity.Companion.SOURCE
import com.monvla.powerbuilderassistant.MainActivity.Companion.SERVICE
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils

class RealTimeTrainingService : Service() {

    companion object {
        const val TRAINING_STATUS = "GET_CURRENT_TIME"
        const val TIME_ARG = "TIME"
    }

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private lateinit var notificationManager: NotificationManager

    private var isRunning = false
    private var isPaused = false
    private var time = 0L

    private fun sendDataToActivity() {
        val sendTime = Intent()
        sendTime.action = TRAINING_STATUS
        sendTime.putExtra(TIME_ARG, time)
        sendBroadcast(sendTime)
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        var waitingTimestamp = 0L

        fun needsUpdate() = (System.currentTimeMillis() - waitingTimestamp) > 1000

        override fun handleMessage(msg: Message) {
            try {
                waitingTimestamp = System.currentTimeMillis()
                while (isRunning) {
                    if (isPaused) continue
                    if (needsUpdate()) {
                        time++
                        updateNotifactionTime(time)
                        sendDataToActivity()
                        waitingTimestamp = System.currentTimeMillis()
                    }
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            stopSelf(msg.arg1)
        }
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        createNotificationChannel()

    }

    fun updateNotifactionTime(time: Long) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(SOURCE, SERVICE)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val formattedTime = Utils.getFormattedTimeFromSeconds(time)
        val notification = getNotification(formattedTime, pendingIntent)

        startForeground(RealTimeTrainingFragment.NOTIFICATION_ID, notification)
        notificationManager.notify(RealTimeTrainingFragment.NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {

        fun getService() = this@RealTimeTrainingService

    }

    fun stopService() {
        isRunning = false
    }

    fun pause() {
        isPaused = true
    }

    fun unpause() {
        isPaused = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chan"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RealTimeTrainingFragment.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(formattedTime: String, pendingIntent: PendingIntent) = NotificationCompat.Builder(
        applicationContext,
        RealTimeTrainingFragment.CHANNEL_ID
    ).apply {
        setSmallIcon(R.mipmap.ic_launcher)
        setContentTitle("Real time training")
        setContentText(formattedTime)
        setContentIntent(pendingIntent)
        setShowWhen(false)
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }.build()
}