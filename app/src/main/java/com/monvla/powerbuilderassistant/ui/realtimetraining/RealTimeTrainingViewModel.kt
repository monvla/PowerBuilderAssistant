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


    var setsCounter = 0
    var totalTime = 0L
    var startTime = 0L

    val trainingSets = mutableListOf<TrainingSet>()
    val trainingExercises = mutableListOf<DairyRecordViewModel.TrainingSet>()
    var isTimerStopped = false

    private val _exercises = MutableLiveData<List<ExerciseEntity>>()

    fun timerTick(time: Long) {
        totalTime = time
        _state.value = State.Update(totalTime, setsCounter, trainingSets)
    }

    fun dropData() {
        trainingSets.clear()
        setsCounter = 0
    }

    fun initialize() {
        viewModelScope.launch {
            _exercises.value = repository.getAllExercises()
        }
    }

    fun start() {
        dropData()
        addSet()
        startTime = System.currentTimeMillis()
        _state.value = State.InProgress
        _state.value = State.Update(totalTime, setsCounter, trainingSets)
    }

    private fun fillSet(setExercises: MutableList<SetResultDialogFragment.SetExercise>) =
            trainingSets.filter { it.number == setsCounter }.map { it.exercises = setExercises }

    fun trainingSetDone(setExercises: MutableList<SetResultDialogFragment.SetExercise>) {
        fillSet(setExercises)
        addSet()
    }

    fun addSet() {
        setsCounter++
        trainingSets.add(TrainingSet(setsCounter, totalTime))
        _state.value = State.Update(totalTime, setsCounter, trainingSets)
    }

    fun trainingDone(setExercises: MutableList<SetResultDialogFragment.SetExercise>) {
        fillSet(setExercises)
        _state.value = State.Finished
        saveTraining()
    }

    fun timerStopped() {
        isTimerStopped = true
    }

    private fun saveTraining() {
        viewModelScope.launch {
            val trainingId = repository.insertTrainingRecord(
                TrainingRecordEntity(date = startTime, length = totalTime)
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