package com.monvla.powerbuilderassistant.ui.exerciseset

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class TrainingSetResultViewModel(application: Application, val setId: Long) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    data class DialogData(val exercisesList: List<ExerciseEntity>, val currentExercise: SetExercise?)

    data class TrainingSetData(val setNumber: Int, val setExercises: List<SetExercise>)

    val showAddExerciseDialog = MediatorLiveData<DialogData>()

    private lateinit var exercises: List<ExerciseEntity>
    private lateinit var setExercises: List<SetExercise>
    private var setNumber by Delegates.notNull<Int>()

    private val _setData = liveData {
        setNumber = repository.getSetById(setId).number
        setExercises = getSetExercises()
        emit(
            TrainingSetData(setNumber, setExercises)
        )
    } as MutableLiveData
    val setData = _setData as LiveData<TrainingSetData>

    private val _deleteTrigger = MutableLiveData<Unit>()
    val deleteTrigger = _deleteTrigger as LiveData<Unit>

    fun exerciseUpdated(updatedExercise: SetExercise) {
        val oldExercise = setExercises.find { it.id == updatedExercise.id }
        val exerciseEntityId = exercises.firstOrNull { it.name == updatedExercise.name }?.id
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

    fun prepareNewExerciseDialog(exercise: SetExercise?) {
        showAddExerciseDialog.addSource(repository.getAllExercises()) {
            showAddExerciseDialog.value = DialogData(it, exercise)
            exercises = it
        }
    }

    fun deleteSetExerciseRequested(exercise: SetExercise) {
        val exerciseEntityId = exercises.firstOrNull { it.name == exercise.name }?.id
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

    private suspend fun getSetExercises(): List<SetExercise> {
        return repository.getSetExercisesBySetId(setId).map {
            SetExercise(
                name = repository.getExerciseById(it.exerciseId).name,
                repeats = it.repeats,
                weight = it.weight,
                id = it.id,
                setId = it.setId
            )
        }
    }

    private suspend fun updateSetExercises() {
        setExercises = repository.getSetExercisesBySetId(setId).map {
            SetExercise(
                name = repository.getExerciseById(it.exerciseId).name,
                repeats = it.repeats,
                weight = it.weight,
                id = it.id,
                setId = it.setId
            )
        }
        _setData.value = TrainingSetData(setNumber, setExercises)
    }
}