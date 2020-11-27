package com.monvla.powerbuilderassistant.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "set_exercise")
class SetExerciseEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "set_id") var setId: Long,
    @ColumnInfo(name = "exercise_id") var exerciseId: Long,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "repeats") val repeats: Int
) {
    companion object {

        fun fromSetExercise(setExercise: SetExercise, exerciseId: Long) = SetExerciseEntity(
                setId = requireNotNull(setExercise.setId),
                exerciseId = exerciseId,
                weight = setExercise.weight,
                repeats = setExercise.repeats
            ).apply {
                setExercise.id?.let {
                    id = it
                }
        }
    }
}
