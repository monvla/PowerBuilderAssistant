package com.monvla.powerbuilderassistant.ui.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import java.util.*
import kotlin.concurrent.timer

class RealTimeTrainingViewModel : ViewModel() {

    data class TrainingSet(val number: Int, val time: Long)


    private var mytimer: Timer? = null
    private val workManager = WorkManager.getInstance()

    private var _currentTime = MutableLiveData(0L)
    var currentTime = _currentTime as LiveData<Long>
    private var _setsCounter = MutableLiveData(0)
    var setsCounter = _setsCounter as LiveData<*>
//    private var _myDataset = MutableLiveData<ArrayList<TrainingSet?>>(arrayListOf())
//    var myDataset = _myDataset as LiveData<ArrayList<TrainingSet?>>

    val _myDataset = MutableLiveData(mutableListOf<TrainingSet>())
    val myDataset: LiveData<MutableList<TrainingSet>> = _myDataset

    var service: RealTimeTrainingService? = null

    //задача для таймера
    inner class UpdateTimeTask() : TimerTask() {
        override fun run() {
            _currentTime.postValue(_currentTime.value?.plus(1))
        }
    }

    fun timerTick() {
        _currentTime.value = _currentTime.value?.plus(1)
    }

    fun updateTimer(time: Long) {
        _currentTime.value = time
    }

    fun dropData() {
        _myDataset.value?.clear()
        _setsCounter.value = 0
        _currentTime.value = 0
    }

    fun stopTimer() {
        mytimer?.cancel()
        mytimer = null
    }

    fun start() {
        dropData()
        addSet()
        startTimer()
    }

    fun startTimer() {
//        if (mytimer == null) {
//            mytimer = timer("TEST", true, 0L, 1000L) {
//                _currentTime.postValue(_currentTime.value?.plus(1))
//            }
//        }
//        mytimer = Timer()
//        mytimer?.schedule(UpdateTimeTask(), 0L, 1000L) //тикаем каждую секунду без задержки
    }

    fun addSet() {
        _setsCounter.value = _setsCounter.value?.plus(1)
        _myDataset.value?.add(TrainingSet(_setsCounter.value!!, _currentTime.value!!))
        _myDataset.value = _myDataset.value
    }

}