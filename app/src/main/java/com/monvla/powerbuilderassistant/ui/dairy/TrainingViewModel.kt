package com.monvla.powerbuilderassistant.ui.dairy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.vo.TrainingRecord

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    val trainingRecords = repository.getAllTraining()

    suspend fun clearAll() {
        repository.clearAll()
    }

    suspend fun getAllTrainings() = repository.getAllTraining()

    suspend fun getAllExercises() = repository.getAllExercises()

//    suspend fun getExerciseByDairyExercise(trainingExercise: TrainingRecordWithExercises): ExerciseEntity? {
//        return repository.getExerciseById(trainingExercise.exerciseId)
//    }

    fun selectExercise() {
    }
}
