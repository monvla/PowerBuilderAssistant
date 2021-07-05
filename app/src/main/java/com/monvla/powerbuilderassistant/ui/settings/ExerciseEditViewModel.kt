package com.monvla.powerbuilderassistant.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.coroutines.launch

class ExerciseEditViewModel(application: Application, exerciseId: Long?) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    private val _exercise = liveData {
        if (exerciseId != null) {
            emit(repository.getExerciseById(exerciseId))
        } else {
            emit(null)
        }
    } as MutableLiveData<ExerciseEntity?>

    val exercise = _exercise as LiveData<ExerciseEntity?>

    private val _changed = MutableLiveData(Unit)
    val changed = _changed as LiveData<Unit>

    fun addExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.insertExercise(exercise)
            _changed.value = Unit
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
            _changed.value = Unit
        }
    }

    fun deleteExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.deleteExerciseSetsByExerciseId(exercise.id)
            repository.deleteExercise(exercise)
            _changed.value = Unit
        }
    }
}