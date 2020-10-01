package com.monvla.powerbuilderassistant.ui.record

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader

class DairyRecordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    val training = MutableLiveData<Training>()

    fun getTrainingData() {
        viewModelScope.launch {
            val trainingEntity = repository.getTrainingById(1)
            trainingEntity?.let{
                val trainingTemp = Training(date = trainingEntity.date, length = trainingEntity.length)
                val sets = repository.getSetsByTrainingId(1)
                sets.forEach {setEntity ->
                    val set = TrainingSet2()
                    val exercises = repository.getSetExercisesBySetId(setEntity.id)
                    exercises.forEach {exerciseEntity ->
                        val exerciseName = repository.getExerciseById(exerciseEntity.exerciseId).name
                        val exercise = Exercise(exerciseName, exerciseEntity.repeats, exerciseEntity.weight)
                        set.exercises?.add(exercise)
                    }
                    trainingTemp.trainingSets?.add(set)
                }
                training.value = trainingTemp
            }
        }
    }

    data class Training(
        val date: Long,
        val length: Long,
        val trainingSets: MutableList<TrainingSet2> = mutableListOf()
    )

    data class TrainingSet2(
        val exercises: MutableList<Exercise> = mutableListOf()
    )

    data class Exercise(
        val name: String,
        val repeats: Int,
        val weight: Float
    )

}

