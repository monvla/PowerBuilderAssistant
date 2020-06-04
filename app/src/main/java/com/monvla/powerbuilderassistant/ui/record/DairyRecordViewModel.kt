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

    fun getExercisesList(resources: Resources): List<Exercise> {
        val json = Json(JsonConfiguration.Stable)
        val exerciseJson = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.exercise_list))).readText()
        return json.parse(Exercise.serializer().list, exerciseJson)
    }

    private val _selectedExercises = MutableLiveData(mutableListOf<Exercise>())
    val selectedExercises: LiveData<MutableList<Exercise>> = _selectedExercises

    fun addExercise(record: Exercise) {
        _selectedExercises.value?.add(record)
    }

    fun getTrainings() = repository.getAllTraining()

    fun createRecord() {
        viewModelScope.launch {
            val training = TrainingRecord(dateTimestamp = 1586241731)
            repository.insertTrainingRecord(training)
        }

    }

    @Serializable
    data class Exercise(
        val id: Int = -1,
        val name: String,
        val weight: Float = 0f,
        val repeats: Int = 0
    )

}

