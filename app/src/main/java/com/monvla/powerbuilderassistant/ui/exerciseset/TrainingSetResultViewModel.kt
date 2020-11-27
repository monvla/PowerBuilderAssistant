package com.monvla.powerbuilderassistant.ui.exerciseset

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import kotlinx.coroutines.launch

class TrainingSetResultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    val exercises = repository.getAllExercises()

    private val _setExercises = MutableLiveData<List<SetExercise>>()
    val setExercises = _setExercises as LiveData<List<SetExercise>>

    private val _setNumber = MutableLiveData<Int>()
    val setNumber = _setNumber as LiveData<Int>

    private val _deleteTrigger = MutableLiveData<Unit>()
    val deleteTrigger = _deleteTrigger as LiveData<Unit>

    var setId: Long? = null

    fun exerciseUpdated(updatedExercise: SetExercise) {
        val oldExercise = _setExercises.value?.find { it.id == updatedExercise.id }
        val exerciseEntityId = exercises.value?.firstOrNull { it.name == updatedExercise.name }?.id
        val setExerciseEntity = SetExerciseEntity.fromSetExercise(
            updatedExercise, requireNotNull(exerciseEntityId)
        )
        viewModelScope.launch {
            if (oldExercise != null) {
                repository.updateSetExercise(setExerciseEntity)
            } else {
                repository.insertSetExercise(setExerciseEntity)
            }
            updateSetExercises()
        }
    }

    private suspend fun updateSetExercises() {
        val setExercises = repository.getSetExercisesBySetId(requireNotNull(setId)).map {
            SetExercise(
                name = repository.getExerciseById(it.exerciseId).name,
                repeats = it.repeats,
                weight = it.weight,
                id = it.id,
                setId = it.setId
            )
        }
        _setExercises.value = setExercises
    }

    fun loadSetExercises(setId: Long) {
        viewModelScope.launch {
            this@TrainingSetResultViewModel.setId = setId
            _setNumber.value = repository.getSetById(setId).number
            updateSetExercises()
        }
    }

    fun deleteSetExercise(exercise: SetExercise) {
        val exerciseEntityId = exercises.value?.firstOrNull { it.name == exercise.name }?.id
        val setExerciseEntity = SetExerciseEntity.fromSetExercise(
            exercise, requireNotNull(exerciseEntityId)
        )
        viewModelScope.launch {
            val deletedRows = repository.deleteSetExercise(setExerciseEntity)
            if (deletedRows != 0) {
                _deleteTrigger.value = Unit
                updateSetExercises()
            }
        }
    }

}