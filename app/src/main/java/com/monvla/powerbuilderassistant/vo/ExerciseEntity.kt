package com.monvla.powerbuilderassistant.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    @ColumnInfo(name = "training_record_id") val trainingRecordId: Int,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "repeats") val repeats: Int,
    val name: String
)