package com.monvla.powerbuilderassistant.repository

import com.monvla.powerbuilderassistant.db.TrainingDao
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity

class TrainingRepository(private val trainingDao: TrainingDao) {

    suspend fun getAllExercises() = trainingDao.getAllExercises()

    suspend fun getAllTraining() = trainingDao.getAllTraining()
    suspend fun getAllSetExercises() = trainingDao.getAllSetExercises()
    suspend fun getAllSets() = trainingDao.getAllSets()

    suspend fun getTrainingById(id: Long) = trainingDao.getTrainingById(id)
    suspend fun getSetsByTrainingId(id: Long) = trainingDao.getSetsByTrainingId(id)
    suspend fun getSetExercisesBySetId(id: Long) = trainingDao.getSetExercisesBySetId(id)
    suspend fun getExerciseById(id: Long) = trainingDao.getExerciseById(id)
    suspend fun getExerciseByName(name: String) = trainingDao.getExerciseByName(name)

    suspend fun insertTrainingRecord(trainingRecord: TrainingRecordEntity) = trainingDao.insertTraining(trainingRecord)

    suspend fun insertExercise(exercise: ExerciseEntity) = trainingDao.insertExercise(exercise)
    suspend fun updateExercise(exercise: ExerciseEntity) = trainingDao.updateExercise(exercise)
    suspend fun insertSetExercise(exercise: SetExerciseEntity) = trainingDao.insertSetExercise(exercise)
    suspend fun insertSet(set: SetEntity) = trainingDao.insertSet(set)

    suspend fun deleteExercise(exercise: ExerciseEntity) = trainingDao.deleteExercise(exercise)
    suspend fun deleteExerciseSetsByExerciseId(exerciseId: Long) = trainingDao.deleteExerciseSetsByExerciseId(exerciseId)

    suspend fun deleteTraining(trainingId: Long) {
        trainingDao.deleteTrainingRecord(trainingId)
    }

    suspend fun getLastTrainingRowId() = trainingDao.getLastTrainingRowId()

    suspend fun updateTraining(trainingId: Long) {

    }

    suspend fun clearAll() {
        trainingDao.deleteExercises()
        trainingDao.deleteAllTrainingRecords()
    }

}