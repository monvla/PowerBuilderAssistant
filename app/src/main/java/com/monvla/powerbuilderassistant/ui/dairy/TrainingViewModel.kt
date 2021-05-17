package com.monvla.powerbuilderassistant.ui.dairy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import java.util.*
import kotlinx.coroutines.launch

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    private val _trainingRecords = MutableLiveData<List<TrainingRecordEntity>>()//repository.getAllTraining()
    var trainingRecords = _trainingRecords as LiveData<List<TrainingRecordEntity>>
    val allTrainings = repository.getAllTraining()

    fun dateSelected(timestamp: Long) {
        val nextDayTimestamp = Calendar.getInstance().apply {
            timeInMillis = timestamp
            roll(Calendar.DAY_OF_MONTH, true)
        }.timeInMillis

        viewModelScope.launch {
            _trainingRecords.value = repository.getTrainingsForDateInterval(timestamp, nextDayTimestamp)
        }
    }
}
