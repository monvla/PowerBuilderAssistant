package com.monvla.powerbuilderassistant.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecord

@Dao
interface TrainingDao {

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM training")
    fun getAllTraining(): LiveData<List<TrainingRecord>>

    @Query("SELECT * FROM exercise WHERE training_record_id = :id")
    fun getExercisesForTrainingRecord(id: Long): LiveData<List<ExerciseEntity>>

    @Insert
    suspend fun insertTraining(trainingRecord: TrainingRecord): Long

    @Insert
    suspend fun insertExercise(exerciseEntity: ExerciseEntity)

    @Query("DELETE FROM exercise")
    suspend fun deleteExercises()

    @Query("DELETE FROM training WHERE id = :id")
    suspend fun deleteTrainingRecord(id: Long): Int

    @Query("DELETE FROM exercise WHERE training_record_id = :trainingId")
    suspend fun deleteExercisesForTraining(trainingId: Long): Int

    @Query("DELETE FROM training")
    suspend fun deleteAllTrainingRecords()
}