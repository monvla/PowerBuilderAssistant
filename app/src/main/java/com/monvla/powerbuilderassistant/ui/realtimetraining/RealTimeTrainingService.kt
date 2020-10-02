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
import android.util.Log
import androidx.core.app.NotificationCompat
import com.monvla.powerbuilderassistant.MainActivity
import com.monvla.powerbuilderassistant.R

class RealTimeTrainingService : Service() {

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private lateinit var notificationManager: NotificationManager

    private var isRunning = false
    private var isPaused = false
    private var time = 0L

    private fun sendDataToActivity() {
        val sendTime = Intent()
        sendTime.action = "GET_CURRENT_TIME"
        sendTime.putExtra("TIME", time)
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
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        createNotificationChannel()

    }

    fun updateNotifactionTime(time: Long) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("destination", "RealTimeTrainingFragment")
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val formattedTime = RealTimeTrainingFragment.getFormattedTime(time)
        val notification = getNotification(formattedTime, pendingIntent)

        startForeground(RealTimeTrainingFragment.NOTIFICATION_ID, notification)
        notificationManager.notify(RealTimeTrainingFragment.NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

//    override fun onBind(intent: Intent): IBinder? {
//        // We don't provide binding, so return null
//        return null
//    }

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

    override fun onDestroy() {
//        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "chan"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(RealTimeTrainingFragment.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
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