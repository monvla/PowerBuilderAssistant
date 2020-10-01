package com.monvla.powerbuilderassistant.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity

@Dao
interface TrainingDao {

    @Query("SELECT * FROM exercise")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM training")
    suspend fun getAllTraining(): List<TrainingRecordEntity>

    @Query("SELECT * FROM set_exercise")
    suspend fun getAllSetExercises(): List<SetExerciseEntity>

    @Query("SELECT * FROM set_table")
    suspend fun getAllSets(): List<SetEntity>

    @Query("SELECT * FROM training WHERE id = :id")
    suspend fun getTrainingById(id: Long): TrainingRecordEntity?

    @Query("SELECT * FROM set_table WHERE training_record_id = :id")
    suspend fun getSetsByTrainingId(id: Long): List<SetEntity>

    @Query("SELECT * FROM set_exercise WHERE set_id = :id")
    suspend fun getSetExercisesBySetId(id: Long): List<SetExerciseEntity>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity

    @Insert
    suspend fun insertTraining(trainingRecord: TrainingRecordEntity): Long

    @Insert
    suspend fun insertExercise(exerciseEntity: ExerciseEntity)

    @Insert
    suspend fun insertSet(setEntity: SetEntity): Long

    @Insert
    suspend fun insertSetExercise(setExerciseEntity: SetExerciseEntity)

    @Query("SELECT MAX(id) FROM training;")
    suspend fun getLastTrainingRowId(): Long?

    @Query("SELECT * FROM exercise WHERE name = :name")
    suspend fun getExerciseByName(name: String): ExerciseEntity

    @Query("DELETE FROM exercise")
    suspend fun deleteExercises()

    @Query("DELETE FROM training WHERE id = :id")
    suspend fun deleteTrainingRecord(id: Long): Int

    @Query("DELETE FROM training")
    suspend fun deleteAllTrainingRecords()
}