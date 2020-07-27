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
import com.monvla.powerbuilderassistant.vo.TrainingRecord
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

    fun getSelectableExercisesList(resources: Resources): List<ExerciseJson> {
        val json = Json(JsonConfiguration.Stable)
        val exerciseJson = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.exercise_list))).readText()
        return json.parse(ExerciseJson.serializer().list, exerciseJson)
    }

    private val _selectedExercises = MutableLiveData(mutableListOf<ExerciseEntity>())
    val selectedExercises: LiveData<MutableList<ExerciseEntity>> = _selectedExercises

    private val _deleteTraining: MutableLiveData<Unit> = MutableLiveData()
    val deleteTraining: LiveData<Unit> = _deleteTraining

    fun addExercise(record: ExerciseEntity) {
        _selectedExercises.value?.add(record)
    }

    fun getExercisesForTraining(trainingId: Long) = repository.getExercisesForTraining(trainingId)

    private fun clearSelectedExercises() {
        _selectedExercises.value = mutableListOf()
    }

    fun deleteTrainingPressed(trainingId: Long) {
        viewModelScope.launch {
            repository.deleteTraining(trainingId)
            _deleteTraining.value = Unit
        }
    }

    fun createRecord() {
        viewModelScope.launch {
            val training = TrainingRecord(dateTimestamp = System.currentTimeMillis())
            val trainingId = repository.insertTrainingRecord(training)
            _selectedExercises.value?.let {
                for (exercise in it) {
                    exercise.trainingRecordId = trainingId
                    repository.insertExercise(exercise)
                }
            }
            clearSelectedExercises()
        }
    }

    @Serializable
    data class ExerciseJson(
        val id: Int = -1,
        val name: String
    )

}

