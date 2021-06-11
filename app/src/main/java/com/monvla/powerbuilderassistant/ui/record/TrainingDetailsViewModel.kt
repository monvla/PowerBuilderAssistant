package com.monvla.powerbuilderassistant.ui.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.*
import kotlinx.coroutines.launch

class TrainingDetailsViewModel(application: Application, val trainingId: Long) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    data class AddSetTrigger(val setNumber: Int)

    private val _recordDeletedTrigger = MutableLiveData(Unit)
    val recordDeletedTrigger = _recordDeletedTrigger as LiveData<Unit>

    private val _dataUpdatedTrigger = MutableLiveData(Unit)
    val dataUpdatedTrigger = _dataUpdatedTrigger as LiveData<Unit>

    private suspend fun getTrainingInfo(trainingId: Long) : TrainingInfo {
        val trainingEntity = repository.getTrainingById(trainingId)
        val trainingTemp = TrainingInfo(date = trainingEntity.date, length = trainingEntity.length)
        val sets = repository.getSetsByTrainingId(trainingId)
        sets.forEach {setEntity ->
            val set = TrainingSet(setEntity.id, setEntity.number)
            val exercises = repository.getSetExercisesBySetId(setEntity.id)
            exercises.forEach {exerciseEntity ->
                val exerciseName = repository.getExerciseById(exerciseEntity.exerciseId).name
                val exercise = Exercise(exerciseName, exerciseEntity.repeats, exerciseEntity.weight)
                set.exercises.add(exercise)
            }
            trainingTemp.trainingSets.add(set)
        }
        return trainingTemp
    }

    val _trainingInfo = liveData { emit(getTrainingInfo(trainingId)) } as MutableLiveData<TrainingInfo>
    val trainingInfo = _trainingInfo as LiveData<TrainingInfo>

    private val _addSetTrigger = MutableLiveData<AddSetTrigger>()
    val addSetTrigger = _addSetTrigger as LiveData<AddSetTrigger>

    fun addSetRequested(trainingId: Long, setNumber: Int) {
        viewModelScope.launch {
            _addSetTrigger.value = AddSetTrigger(setNumber)
            _dataUpdatedTrigger.value = Unit
        }
    }

    fun resumed() {
        viewModelScope.launch {
            _trainingInfo.value = getTrainingInfo(trainingId)
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            val exercises = repository.getSetExercisesBySetId(setId)
            exercises.forEach { exercise ->
                repository.deleteSetExercise(exercise)
            }
            val set = repository.getSetById(setId)
            repository.deleteSet(set)
            _trainingInfo.value = getTrainingInfo(trainingId)
        }
    }

    fun setUpdated(setId: Long, setExercisesList: SetExercisesList, setNumber: Int) {
        viewModelScope.launch {
            if (setId == -1L) {
                addSet(setExercisesList, setNumber)
            } else {
                updateSetExercise(setId, setExercisesList)
            }
            _trainingInfo.value = getTrainingInfo(trainingId)
        }
    }

    fun deleteRecord() {
        viewModelScope.launch {
            val sets = repository.getSetsByTrainingId(trainingId)
            sets.forEach { setEntity ->
                val exercises = repository.getSetExercisesBySetId(setEntity.id)
                exercises.forEach { exercise ->
                    repository.deleteSetExercise(exercise)
                }
                repository.deleteSet(setEntity)
            }
            repository.deleteTraining(trainingId)
            _recordDeletedTrigger.value = Unit
        }
    }

    private suspend fun updateSetExercise(setId: Long, setExercisesList: SetExercisesList) {
        repository.getSetExercisesBySetId(setId).forEach {
            repository.deleteSetExercise(it)
        }
        insertSetExercises(setId, setExercisesList)
    }

    private suspend fun insertSetExercises(setId: Long, setExercisesList: SetExercisesList) {
        setExercisesList.forEach {
            val setExerciseEntity = if (it.dbId != UNDEFINED_ID) {
                SetExerciseEntity(it.dbId, setId, it.exerciseId, it.weight, it.repeats)
            } else {
                val exercise = repository.getExerciseByName(it.exerciseName)
                SetExerciseEntity(setId = setId, exerciseId = exercise.id, weight = it.weight, repeats = it.repeats)
            }
            repository.insertSetExercise(setExerciseEntity)
        }
    }

    private suspend fun addSet(setExercisesList: SetExercisesList, setNumber: Int) {
        val newSetId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = setNumber))
        insertSetExercises(newSetId, setExercisesList)
    }

    data class TrainingInfo(
        val date: Long,
        val length: Long,
        val trainingSets: MutableList<TrainingSet> = mutableListOf()
    ) {

        fun getAverageSetLength() = if (trainingSets.size > 0) length / trainingSets.size else 0

        fun getTotalWeight(): Float {
            var totalWeight = 0f
            trainingSets.forEach {set ->
                set.exercises.forEach { totalWeight += it.weight * it.repeats }
            }
            return totalWeight
        }
    }
}
