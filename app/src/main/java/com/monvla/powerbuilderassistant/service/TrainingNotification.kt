package com.monvla.powerbuilderassistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.monvla.powerbuilderassistant.MainActivity
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment

class TrainingNotification(val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val NOTIFICATION_ID = 1337
    }

    private val intent = Intent(context, MainActivity::class.java).apply {
        putExtra(MainActivity.SOURCE, MainActivity.SERVICE)
    }

    private val pendingIntent = PendingIntent.getActivity(
        context, 0,
        intent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    init {
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

    fun notify(formattedTime: String) {
        notificationManager.notify(NOTIFICATION_ID, getNotification(formattedTime))
    }

    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun getNotification(formattedTime: String): Notification {
        return NotificationCompat.Builder(
            context,
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
}