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

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrainingRepository

    init {
        val exerciseDao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(exerciseDao)
    }

    private val _recordDeleted = MutableLiveData(Unit)
    val recordDeleted = _recordDeleted as LiveData<Unit>

    val exercises = repository.getAllExercises()

    val training = MutableLiveData<Training>()

    fun getTrainingData(trainingId: Long) {
        viewModelScope.launch {
            val trainingEntity = repository.getTrainingById(trainingId)
            trainingEntity?.let{
                val trainingTemp = Training(date = trainingEntity.date, length = trainingEntity.length)
                val sets = repository.getSetsByTrainingId(trainingId)
                sets.forEach {setEntity ->
                    val set = TrainingSet(setEntity.id, setEntity.number)
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

    fun deleteRecord(trainingId: Long) {
        viewModelScope.launch {
            val sets = repository.getSetsByTrainingId(trainingId)
            sets.forEach { setEntity ->
                val exercises = repository.getSetExercisesBySetId(setEntity.id)
                exercises.forEach { exercise ->
                    repository.deleteSetExercise(exercise)
                }
                repository.deleteSet(setEntity)
            }
            repository.deleteTraining(trainingId)
            _recordDeleted.value = Unit
        }

    }

    data class Training(
        val date: Long,
        val length: Long,
        val trainingSets: MutableList<TrainingSet> = mutableListOf()
    ) {

        fun getAverageSetLength() = if (trainingSets.size > 0) length / trainingSets.size else 0

        fun getTotalWeight(): Float {
            var totalWeight = 0f
            trainingSets.forEach {set ->
                set.exercises.forEach { totalWeight += it.weight * it.repeats }
            }
            return totalWeight
        }
    }

    data class TrainingSet(
        val id: Long,
        val number: Int,
        val exercises: MutableList<Exercise> = mutableListOf()
    )

    data class Exercise(
        val name: String,
        val repeats: Int,
        val weight: Float
    )

}

