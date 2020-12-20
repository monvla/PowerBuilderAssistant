package com.monvla.powerbuilderassistant.statistics.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository

class ExerciseStatisticsViewModel(application: Application, val exerciseId: Long) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    data class StatisticsData(val date: Long, val repeats: Int)

    val statisticsData: LiveData<List<StatisticsData>> = repository.getExerciseStatistics(exerciseId).switchMap { exerciseStatisticsList ->
        liveData {
            if (exerciseStatisticsList.isEmpty()) {
                emit(emptyList())
                return@liveData
            }
            var lastTrainingDate = exerciseStatisticsList[0].date
            var repeatsSum = 0
            var repeatsCount = 0
            val statisticsDataList = mutableListOf<StatisticsData>()
            exerciseStatisticsList.forEach {
                if (lastTrainingDate != it.date) {
                    lastTrainingDate = it.date
                    statisticsDataList.add(StatisticsData(it.date, repeatsSum / repeatsCount))
                    repeatsSum = 0
                    repeatsCount = 0
                }
                repeatsSum += it.repeats
                repeatsCount++
            }
            statisticsDataList.add(StatisticsData(exerciseStatisticsList.last().date, repeatsSum / repeatsCount))
            emit(statisticsDataList as List<StatisticsData>)
        }
    }

}