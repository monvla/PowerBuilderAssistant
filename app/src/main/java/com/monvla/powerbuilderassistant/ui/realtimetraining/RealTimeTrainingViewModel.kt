package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.coroutines.launch

class RealTimeTrainingViewModel(application: Application) : AndroidViewModel(application) {

    data class TrainingSet(val number: Int, val time: Long)

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
    var currentTime = 0L

    val trainingSetsData = mutableListOf<TrainingSet>()
    var isTimerStopped = false

    private val _exercises = MutableLiveData<List<ExerciseEntity>>()

    var nextTrainingId: Long? = null

    fun timerTick(time: Long) {
        currentTime = time
        _state.value = State.Update(currentTime, setsCounter, trainingSetsData)
    }

    fun dropData() {
        trainingSetsData.clear()
        setsCounter = 0
    }

    fun initialize() {
        viewModelScope.launch {
            _exercises.value = repository.getAllExercises()
            nextTrainingId = (repository.getLastTrainingRowId() ?: 0) + 1
        }
    }

    fun start() {
        dropData()
        addSet()
        _state.value = State.InProgress
    }

    fun addSet() {
        setsCounter++
        trainingSetsData.add(TrainingSet(setsCounter, currentTime))
        _state.value = State.Update(currentTime, setsCounter, trainingSetsData)
    }

    fun finishTraining() {
        _state.value = State.Finished
        saveTraining()
    }

    fun timerStopped() {
        isTimerStopped = true
    }

    private fun saveTraining() {
        viewModelScope.launch {
            repository.insertTrainingRecord(TrainingRecordEntity(date = System.currentTimeMillis(), length = currentTime))
        }
    }

    fun saveSet(data: List<SetResultDialogFragment.TrainingSetData>) {
        viewModelScope.launch {
            nextTrainingId?.let {trainingId ->
                val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = setsCounter))
                data.forEach {
                    val exercise = repository.getExerciseByName(it.name)
                    repository.insertSetExercise(SetExerciseEntity(setId = setId, weight = it.weight, repeats = it.repeats, exerciseId = exercise.id))
                }
            }
        }
    }

    fun getLoadedExercises() = _exercises.value!!


}