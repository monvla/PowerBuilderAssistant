package com.monvla.powerbuilderassistant.db

import androidx.room.*
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecord
import com.monvla.powerbuilderassistant.vo.TrainingRecordWithExercises

@Dao
interface TrainingDao {

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Transaction
    @Query("SELECT * FROM training")
    suspend fun getTrainingWithExercises(): List<TrainingRecordWithExercises>

    @Insert
    suspend fun insertTraining(trainingRecord: TrainingRecord)

    @Insert
    suspend fun insertExercise(exerciseEntity: ExerciseEntity)

    @Query("DELETE FROM exercise")
    suspend fun deleteExercises()

    @Query("DELETE FROM training")
    suspend fun deleteTraining()
}