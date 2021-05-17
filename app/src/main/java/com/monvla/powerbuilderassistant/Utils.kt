package com.monvla.powerbuilderassistant

import android.content.res.Resources
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
            formatter.timeZone = TimeZone.getTimeZone("GMT+0");
            return formatter.format(date)
        }

        fun getFormattedDateTime(timestamp: Long): String {
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.timeZone = TimeZone.getDefault();
            return formatter.format(date)
        }

        fun getFormattedMonth(timestamp: Long): String {
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("MM.yyyy");
            formatter.timeZone = TimeZone.getDefault();
            return formatter.format(date)
        }

        fun currentTimeSeconds() = System.currentTimeMillis() / 1000

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fun getDateOnlyTimestamp(timestamp: Long): Long {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = sdf.parse(sdf.format(Date(timestamp)))
            return checkNotNull(parsedDate).time
        }
    }

    @Serializable
    data class ExerciseJson(
        val id: Int = -1,
        val name: String
    )

}
