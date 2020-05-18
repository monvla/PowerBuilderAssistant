package com.monvla.powerbuilderassistant.ui.dairy

import android.app.Application
import androidx.lifecycle.*
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.vo.TrainingRecordWithExercises

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    private val _selectedDairyExercise = MutableLiveData<TrainingRecordWithExercises>()
    val selectedDairyExercises = _selectedDairyExercise as LiveData<TrainingRecordWithExercises>

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    suspend fun getTrainingWithExercises(): List<TrainingRecordWithExercises> {
        return repository.getTrainingWithExercises()
    }

//    suspend fun getExerciseByDairyExercise(trainingExercise: TrainingRecordWithExercises): ExerciseEntity? {
//        return repository.getExerciseById(trainingExercise.exerciseId)
//    }

    fun selectExercise(exercise: TrainingRecordWithExercises) {
        _selectedDairyExercise.value = exercise
    }


}