package com.monvla.powerbuilderassistant.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(
    ExerciseEntity::class,
    TrainingRecord::class
), version = 1, exportSchema = false)
public abstract class TrainingRoomDb : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {

        @Volatile
        private var INSTANCE: TrainingRoomDb? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TrainingRoomDb {
            val tempInstance =
                    INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrainingRoomDb::class.java,
                    "training_database"
                    )
                    .addCallback(
                        TrainingRoomCallback(
                            scope,
                            context
                        )
                    )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class TrainingRoomCallback(
        private val scope: CoroutineScope,
        private val context: Context
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            INSTANCE?.let { database ->
                scope.launch {
//                    val dao = database.trainingDao()
//
//                    val ex1 =
//                            ExerciseEntity(trainingRecordId =  5, weight = 10f, repeats = 2, name = "TEST1")
//                    val ex2 =
//                            ExerciseEntity(trainingRecordId =  5, weight = 20f, repeats = 2, name = "TEST2")
//                    val ex3 =
//                            ExerciseEntity(trainingRecordId =  1, weight = 30f, repeats = 2, name = "TEST3")
//                    dao.insertExercise(ex1)
//                    dao.insertExercise(ex2)
//                    dao.insertExercise(ex3)
//                    val training = TrainingRecord(5, 1586241731)
//                    val training2 = TrainingRecord(1, 1586241731)
//                    dao.insertTraining(training)
//                    dao.insertTraining(training2)
//
//                    val trainings = dao.getAllTraining()
//                    val exercises = dao.getAllExercises()
//                    val exercisesForTraining = dao.getExercisesForTrainingRecord(5)
//                    val i = 0
                }
            }
        }
    }
}