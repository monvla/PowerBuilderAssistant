package com.monvla.powerbuilderassistant.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.service.TrainingNotification.Companion.NOTIFICATION_ID
import com.monvla.powerbuilderassistant.vo.ServiceTrainingSet
import timber.log.Timber
import java.lang.ref.WeakReference

class RealTimeTrainingService : Service(), TrainingService {

    companion object {
        const val TRAINING_STATUS = "GET_CURRENT_TIME"
        const val TIME_ARG = "TIME"
        const val RTT_SERVICE_STARTED = "rtt_service_started"
    }

    data class TrainingServiceData(val timeStarted: Long, val cachedTrainingSets: List<ServiceTrainingSet>?)

    private val binder = LocalBinder()

    private var serviceHandler: ServiceHandler? = null
    private lateinit var notification: TrainingNotification

    private var isFinished = false

    private var timePassed = 0L
    private var timeStarted = 0L
    private var cachedTrainingSets: List<ServiceTrainingSet>? = null

    private var listener: TrainingServiceListener? = null

    override fun onCreate() {
        Timber.d("onCreate")
        serviceHandler = ServiceHandler(WeakReference(this))
        notification = TrainingNotification(applicationContext)

        val formattedTime = Utils.getFormattedTimeFromSeconds(timePassed)
        startForeground(NOTIFICATION_ID, notification.getNotification(formattedTime))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Timber.d("onStartCommand")
        timeStarted = System.currentTimeMillis()
        serviceHandler?.obtainMessage()?.also { msg ->
            Timber.d("obtainMessage")
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }
        updateNotificationTime()
        return START_STICKY
    }

    override fun onDestroy() {
        Timber.d("destroy service")
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun isRunning(): Boolean {
        return !isFinished
    }

    override fun updateNotificationTime() {
        timePassed = (System.currentTimeMillis() - timeStarted) / 1000
        val formattedTime = Utils.getFormattedTimeFromSeconds(timePassed)
        notification.notify(formattedTime)
        sendDataToActivity()
    }

    override fun getCachedData(): TrainingServiceData {
        return TrainingServiceData(timeStarted, cachedTrainingSets)
    }

    override fun setCachedTrainingSets(trainingSets: List<ServiceTrainingSet>) {
        cachedTrainingSets = trainingSets
    }

    override fun registerServiceListener(listener: TrainingServiceListener) {
        this.listener = listener
    }

    override fun stop() {
        isFinished = true
        stopSelf()
        notification.cancel()
        listener?.onStateRecieved(false)
        listener = null
    }

    private fun sendDataToActivity() {
        with (Intent()) {
            action = TRAINING_STATUS
            putExtra(TIME_ARG, timePassed)
            sendBroadcast(this)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@RealTimeTrainingService
    }

    open class ServiceConnection : android.content.ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }
}