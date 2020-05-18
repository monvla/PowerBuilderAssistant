package com.monvla.powerbuilderassistant.vo

import androidx.room.Embedded
import androidx.room.Relation

data class TrainingRecordWithExercises(
    @Embedded val training: TrainingRecord,
    @Relation(
        parentColumn = "trainingId",
        entityColumn = "parent_training_id"
    )
    val exercises: List<ExerciseEntity>
)