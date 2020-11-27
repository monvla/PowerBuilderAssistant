package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.ui.record.TrainingViewModel
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
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
        object Finished : State()
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
    val trainingExercises = mutableListOf<TrainingViewModel.TrainingSet>()
    var isTimerStopped = false
    var trainingId: Long = -1L

    val exercises = repository.getAllExercises()

    fun timerTick(time: Long) {
        totalTime = time//Utils.currentTimeSeconds() - startTimestamp / 1000
        _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
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
        _state.value = State.InProgress
        _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
    }

    private fun fillSet(setExercises: MutableList<SetExercise>) =
            trainingSets.filter { it.number == trainingSets.size }.map { it.exercises = setExercises }

    fun trainingSetDone(setExercises: MutableList<SetExercise>) {
        fillSet(setExercises)
        addSet()
    }

    fun resumed() {

    }

    fun addSet() {
        viewModelScope.launch {
            val setNumber = trainingSets.size + 1
            trainingSets.add(TrainingSet(setNumber, totalTime))
            val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = setNumber))
            _state.value = State.FillSet(trainingId, setId)
        }
//        _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
    }

    fun trainingDone(setExercises: MutableList<SetExercise>) {
//        fillSet(setExercises)
        _state.value = State.Finished
//        saveTraining()
    }

    fun timerStopped() {
        isTimerStopped = true
    }

    fun viewCreated() {
        when (_state.value) {
            is State.Finished -> _state.value = State.Ready
            is State.FillSet ->
                if (isTimerStopped) {
                    _state.value = State.Finished
                } else {
                    _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
                }
        }
    }

    private fun saveTraining() {
        viewModelScope.launch {
            trainingSets.forEach { data ->
                val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = data.number))
                data.exercises.forEach {
                    val exercise = repository.getExerciseByName(it.name)
                    repository.insertSetExercise(SetExerciseEntity(setId = setId, weight = it.weight, repeats = it.repeats, exerciseId = exercise.id))
                }
            }
        }
    }

//    fun getLoadedExercises() = _exercises.value!!
}