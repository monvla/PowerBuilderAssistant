package com.monvla.powerbuilderassistant.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecord

@Dao
interface TrainingDao {

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM training")
    fun getAllTraining(): LiveData<List<TrainingRecord>>

    @Query("SELECT * FROM exercise WHERE training_record_id = :id")
    suspend fun getExercisesForTrainingRecord(id: Int): List<ExerciseEntity>

    @Insert
    suspend fun insertTraining(trainingRecord: TrainingRecord)

    @Insert
    suspend fun insertExercise(exerciseEntity: ExerciseEntity)

    @Query("DELETE FROM exercise")
    suspend fun deleteExercises()

    @Query("DELETE FROM training")
    suspend fun deleteTraining()
}