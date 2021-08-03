package com.monvla.powerbuilderassistant.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID
import kotlin.collections.ArrayList

const val UNDEFINED_ID = -1L

@Parcelize
data class SetExercise(
    var exerciseName: String,
    var repeats: Int,
    var weight: Float,
    var uuid: UUID,
    var dbId: Long = UNDEFINED_ID,
    var exerciseId: Long = UNDEFINED_ID,
    var setId: Long = UNDEFINED_ID
) : Parcelable {
    companion object {
        fun create(exerciseName: String, repeats: Int, weight: Float): SetExercise {
            return SetExercise(exerciseName, repeats, weight, UUID.randomUUID())
        }

        fun createEmpty(): SetExercise {
            return create("", 0, 0f)
        }

        fun getSetExerciseFromEntity(exercise: ExerciseEntity, setExerciseEntity: SetExerciseEntity): SetExercise {
            return SetExercise(
                exerciseName = exercise.name,
                repeats = setExerciseEntity.repeats,
                weight = setExerciseEntity.weight,
                uuid = UUID.randomUUID(),
                dbId = setExerciseEntity.id,
                exerciseId = setExerciseEntity.exerciseId,
                setId = setExerciseEntity.setId
            )
        }
    }

    fun update(name: String, repeats: Int, weight: Float, exerciseId: Long): SetExercise {
        this.exerciseName = name
        this.repeats = repeats
        this.weight = weight
        this.exerciseId = exerciseId
        return this
    }
}

@Parcelize
class SetExercisesList : ArrayList<SetExercise>(), Parcelable

data class ServiceTrainingSet(
    val time: Long,
    var exercises: MutableList<SetExercise> = mutableListOf()
)

data class TrainingSet(
    val id: Long,
    val number: Int,
    val exercises: MutableList<Exercise> = mutableListOf()
)

data class Exercise(
    val name: String,
    val repeats: Int,
    val weight: Float
)