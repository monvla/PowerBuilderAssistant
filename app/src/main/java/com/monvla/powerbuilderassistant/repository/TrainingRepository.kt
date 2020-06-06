package com.monvla.powerbuilderassistant.repository

import com.monvla.powerbuilderassistant.db.TrainingDao
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecord

class TrainingRepository(private val trainingDao: TrainingDao) {

    suspend fun getAllExercises() = trainingDao.getAllExercises()

    fun getAllTraining() = trainingDao.getAllTraining()

    fun getExercisesForTraining(trainingId: Long) = trainingDao.getExercisesForTrainingRecord(trainingId)

    suspend fun insertTrainingRecord(trainingRecord: TrainingRecord) = trainingDao.insertTraining(trainingRecord)

    suspend fun insertExercise(exercise: ExerciseEntity) = trainingDao.insertExercise(exercise)

    suspend fun clearAll() {
        trainingDao.deleteExercises()
        trainingDao.deleteTraining()
    }

}