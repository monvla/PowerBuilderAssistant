package com.monvla.powerbuilderassistant.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ExerciseEditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    private val _exercises = MutableLiveData<List<ExerciseEntity>>()
    val exercises = _exercises as LiveData<List<ExerciseEntity>>

    private val _exercise = MutableLiveData<ExerciseEntity>()
    val exercise = _exercise as LiveData<ExerciseEntity>

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
        viewModelScope.launch {
            _exercises.value = repository.getAllExercises()
        }
    }

    fun updateExercise(exercise: ExerciseEntity) {
        viewModelScope.launch {
            repository.updateExercise(exercise)
        }
    }

    fun loadExercise(exerciseId: Long) {
        viewModelScope.launch {
            _exercise.value = repository.getExerciseById(exerciseId)
        }
    }
}