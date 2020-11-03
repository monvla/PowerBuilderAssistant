package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.coroutines.launch

class RealTimeTrainingViewModel(application: Application) : AndroidViewModel(application) {

    data class TrainingSet(
        val number: Int,
        val time: Long,
        var exercises: MutableList<SetResultDialogFragment.SetExercise> = mutableListOf()
    )

    sealed class State {
        object Ready : State()
        object InProgress : State()
        object Finished : State()
        data class Update(val time: Long, val sets: Int, val trainingSets: MutableList<TrainingSet>) : State()
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
    val trainingExercises = mutableListOf<DairyRecordViewModel.TrainingSet>()
    var isTimerStopped = false

    private val _exercises = MutableLiveData<List<ExerciseEntity>>()

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

    fun initialize() {
        viewModelScope.launch {
            _exercises.value = repository.getAllExercises()
        }
    }

    fun start() {
        dropData()
        addSet()
        _state.value = State.InProgress
        _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
    }

    private fun fillSet(setExercises: MutableList<SetResultDialogFragment.SetExercise>) =
            trainingSets.filter { it.number == trainingSets.size }.map { it.exercises = setExercises }

    fun trainingSetDone(setExercises: MutableList<SetResultDialogFragment.SetExercise>) {
        fillSet(setExercises)
        addSet()
    }

    fun addSet() {
        trainingSets.add(TrainingSet(trainingSets.size + 1, totalTime))
        _state.value = State.Update(totalTime, trainingSets.size, trainingSets)
    }

    fun trainingDone(setExercises: MutableList<SetResultDialogFragment.SetExercise>) {
        fillSet(setExercises)
        _state.value = State.Finished
        saveTraining()
    }

    fun timerStopped() {
        isTimerStopped = true
    }

    fun viewCreated() {
        if (_state.value == State.Finished) {
            _state.value = State.Ready
        }
    }

    private fun saveTraining() {
        viewModelScope.launch {
            val trainingId = repository.insertTrainingRecord(
                TrainingRecordEntity(date = startTimestamp, length = totalTime)
            )
            trainingSets.forEach { data ->
                val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = data.number))
                data.exercises.forEach {
                    val exercise = repository.getExerciseByName(it.name)
                    repository.insertSetExercise(SetExerciseEntity(setId = setId, weight = it.weight, repeats = it.repeats, exerciseId = exercise.id))
                }
            }
        }
    }

    fun getLoadedExercises() = _exercises.value!!
}