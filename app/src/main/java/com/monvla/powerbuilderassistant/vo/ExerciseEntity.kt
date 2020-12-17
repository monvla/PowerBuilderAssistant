package com.monvla.powerbuilderassistant.vo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "default_weight") val defaultWeight: Float
)

data class Lupa(
    val set_id: Long,
    val repeats: Int,
    val training_record_id: Long,
    val length: Long

)