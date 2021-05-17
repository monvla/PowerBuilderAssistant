package com.monvla.powerbuilderassistant.service

import com.monvla.powerbuilderassistant.vo.ServiceTrainingSet

interface TrainingService {
    fun isRunning(): Boolean
    fun updateNotificationTime()
    fun getCachedData(): RealTimeTrainingService.TrainingServiceData
    fun setCachedTrainingSets(trainingSets: List<ServiceTrainingSet>)

    fun registerServiceListener(listener: TrainingServiceListener)

    fun stop()
}

interface TrainingServiceListener {
    fun onStateRecieved(value: Boolean)
}