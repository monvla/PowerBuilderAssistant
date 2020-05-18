package com.monvla.powerbuilderassistant.repository

import com.monvla.powerbuilderassistant.db.TrainingDao

class TrainingRepository(private val trainingDao: TrainingDao) {

    suspend fun getExercises() = trainingDao.getAllExercises()

    suspend fun getTrainingWithExercises() = trainingDao.getTrainingWithExercises()

}