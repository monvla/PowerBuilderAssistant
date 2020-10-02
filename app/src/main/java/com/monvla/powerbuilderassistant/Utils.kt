package com.monvla.powerbuilderassistant

import android.content.res.Resources
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class Utils {
    companion object {
        fun getDefaultExercisesList(resources: Resources): List<ExerciseJson> {
            val json = Json(JsonConfiguration.Stable)
            val exerciseJson = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.exercise_list))).readText()
            return json.parse(ExerciseJson.serializer().list, exerciseJson)
        }

        fun getFormattedTimeFromSeconds(time: Long): String {
            val timestamp = TimeUnit.SECONDS.toMillis(time)
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return formatter.format(date)
        }

        fun getFormattedDateTime(timestamp: Long): String {
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getDefault());
            return formatter.format(date)
        }
    }

    @Serializable
    data class ExerciseJson(
        val id: Int = -1,
        val name: String
    )
}
