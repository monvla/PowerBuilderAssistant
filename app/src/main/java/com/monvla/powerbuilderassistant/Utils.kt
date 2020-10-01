package com.monvla.powerbuilderassistant

import android.content.res.Resources
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader


class Utils {
    companion object {
        fun getDefaultExercisesList(resources: Resources): List<ExerciseJson> {
            val json = Json(JsonConfiguration.Stable)
            val exerciseJson = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.exercise_list))).readText()
            return json.parse(ExerciseJson.serializer().list, exerciseJson)
        }
    }

    @Serializable
    data class ExerciseJson(
        val id: Int = -1,
        val name: String
    )
}
