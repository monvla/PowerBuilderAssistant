package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.coroutines.launch

class RealTimeTrainingViewModel(application: Application) : AndroidViewModel(application) {

    data class TrainingSet(
        val number: Int,
        val time: Long,
        var exercises: MutableList<SetExercise> = mutableListOf()
    )

    sealed class State {
        object Ready : State()
        object InProgress : State()
        data class Finished(val setsCounter: Int, val totalTime: Long) : State()
        data class Update(val time: Long, val sets: Int, val trainingSets: MutableList<TrainingSet>) : State()
        data class FillSet(val trainingId: Long, val setId: Long) : State()
    }

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    private var _state: MutableLiveData<State> = MutableLiveData(State.Ready)
    val state = _state as LiveData<State>

    private var totalTime = 0L
    var startTimestamp = 0L

    val trainingSets = mutableListOf<TrainingSet>()
    var isTimerStopped = false
    var trainingId: Long = -1L

    fun timerTick(time: Long) {
        totalTime = time
        _state.value = State.Update(totalTime, getCurrentSetNumber(), trainingSets)
    }

    fun dropData() {
        trainingSets.clear()
        startTimestamp = System.currentTimeMillis()
        totalTime = 0
        isTimerStopped = false
    }

    fun start() {
        viewModelScope.launch {
            trainingId = repository.insertTrainingRecord(
                TrainingRecordEntity(date = startTimestamp, length = totalTime)
            )
        }
        dropData()
        trainingSets.add(TrainingSet(getCurrentSetNumber() + 1, totalTime))
        _state.value = State.InProgress
        _state.value = State.Update(totalTime, getCurrentSetNumber(), trainingSets)
    }

    private fun getCurrentSetNumber() = trainingSets.size

    fun addSet() {
        viewModelScope.launch {
            val setNumber = getCurrentSetNumber()
            val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = setNumber))
            _state.value = State.FillSet(trainingId, setId)
            repository.updateTrainingRecord(
                TrainingRecordEntity(
                    id = trainingId,
                    date = startTimestamp,
                    length = totalTime
                )
            )
            if (!isTimerStopped) {
                startNewSet()
            }
        }
    }

    private fun startNewSet() = trainingSets.add(TrainingSet(getCurrentSetNumber() + 1, totalTime))

    fun timerStopped() {
        isTimerStopped = true
    }

    fun viewCreated() {
        when (_state.value) {
            is State.Finished -> _state.value = State.Ready
            is State.FillSet -> {
                if (isTimerStopped) {
                    _state.value = State.Finished(trainingSets.size, totalTime)
                } else {
                    _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
                }
            }
        }
    }
}