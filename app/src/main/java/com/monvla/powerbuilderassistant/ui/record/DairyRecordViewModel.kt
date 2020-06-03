package com.monvla.powerbuilderassistant.ui.record

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.monvla.powerbuilderassistant.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.collections.ArrayList

class DairyRecordViewModel : ViewModel() {

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

    data class DairyRecord(
        val exerciseId: Int,
        val exerciseName: String,
        val weight: Float,
        val repeats: Int
    )

    @Serializable
    data class Exercise(
        val id: Int,
        val name: String,
        val weight: Float = 0f,
        val repeats: Int = 0
    )

}

