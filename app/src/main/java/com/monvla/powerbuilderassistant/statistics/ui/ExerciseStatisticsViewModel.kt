package com.monvla.powerbuilderassistant.statistics.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository

class ExerciseStatisticsViewModel(application: Application, val setId: Long) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    val data = repository.getLupa()
//            liveData {
//        emit(setId)
//    }

}