package com.monvla.powerbuilderassistant.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "set_exercise")
class SetExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "set_id") var setId: Long,
    @ColumnInfo(name = "exercise_id") var exerciseId: Long,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "repeats") val repeats: Int
)
