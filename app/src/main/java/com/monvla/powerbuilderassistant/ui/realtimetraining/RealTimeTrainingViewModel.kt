package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.*
import java.util.*
import kotlinx.coroutines.launch
import timber.log.Timber

class RealTimeTrainingViewModel(application: Application) : AndroidViewModel(application) {

    sealed class State {
        object Ready : State()
        data class Finished(val setsCounter: Int, val totalTime: Long) : State()
        data class Update(val time: Long, val sets: Int, val trainingSets: MutableList<ServiceTrainingSet>) : State()
    }

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    data class NavigationInfo(val currentSetNumber: Int, val setExercisesList: SetExercisesList)

    private var _navigateToSetResultFragment = MutableLiveData<NavigationInfo>()
    var navigateToSetResultFragment = _navigateToSetResultFragment as LiveData<NavigationInfo>

    private var _state: MutableLiveData<State> = MutableLiveData(State.Ready)
    val state = _state as LiveData<State>

    private var totalTime = 0L
    var startTimestamp = 0L

    private var isTrainingFinished = false
    private var trainingSets = mutableListOf<ServiceTrainingSet>()
    private var addedExercisesUUID = mutableListOf<UUID>()

    fun timerTick(time: Long) {
        totalTime = time
        _state.value = State.Update(totalTime, getCurrentSetNumber(), trainingSets)
    }

    fun trainingStarted() {
        startTimestamp = System.currentTimeMillis()
        _state.value = State.Update(totalTime, getCurrentSetNumber(), trainingSets)
    }

    fun newSetClicked() {
        _navigateToSetResultFragment.value = getNavigationArgSetExercises()
    }

    fun addSet(setExercisesList: SetExercisesList) {
        if (isAlreadyAdded(setExercisesList)) return

        setExercisesList.forEach {
            addedExercisesUUID.add(it.uuid)
        }
        trainingSets.add(ServiceTrainingSet(totalTime, setExercisesList as MutableList<SetExercise>))
        if (isTrainingFinished) {
            _state.value = State.Finished(trainingSets.size, totalTime)

            viewModelScope.launch {
                writeTrainingToDb()
            }
        }
    }

    fun updateTrainingStartTimestamp(startTimestamp: Long) {
        this.startTimestamp = startTimestamp
    }

    fun updateTrainingSets(trainingSets: List<ServiceTrainingSet>) {
        this.trainingSets = trainingSets as MutableList<ServiceTrainingSet>
    }

    fun trainingFinished() {
        isTrainingFinished = true
        _navigateToSetResultFragment.value = getNavigationArgSetExercises()
    }

    private fun getNavigationArgSetExercises() : NavigationInfo {
        return NavigationInfo(getCurrentSetNumber(), SetExercisesList())
    }

    private fun getCurrentSetNumber() = trainingSets.size + 1

    private fun isAlreadyAdded(setExercisesList: SetExercisesList): Boolean {
        setExercisesList.forEach {
            if (addedExercisesUUID.contains(it.uuid)) {
                return true
            }
        }
        return false
    }

    private suspend fun writeTrainingToDb() {
        val trainingRecordId = repository.insertTrainingRecord(
            TrainingRecordEntity(
                date = startTimestamp,
                length = totalTime
            )
        )
        trainingSets.forEachIndexed { index, set ->
            val setId = repository.insertSet(
                SetEntity(
                    trainingRecordId = trainingRecordId,
                    number = index + 1
                )
            )
            set.exercises.forEach { setExercise ->
                val exercise = repository.getExerciseByName(setExercise.exerciseName)
                repository.insertSetExercise(
                    SetExerciseEntity(
                        setId = setId,
                        exerciseId = exercise.id,
                        weight = setExercise.weight,
                        repeats = setExercise.repeats
                    )
                )
            }
        }
    }
}