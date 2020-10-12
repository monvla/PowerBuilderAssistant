package com.monvla.powerbuilderassistant.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    ExerciseEntity::class,
    SetEntity::class,
    SetExerciseEntity::class,
    TrainingRecordEntity::class
], version = 2, exportSchema = false)
abstract class TrainingRoomDb : RoomDatabase() {

    abstract fun trainingDao(): TrainingDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE exercise ADD COLUMN default_weight REAL DEFAULT 0.0 NOT NULL;")
            }
        }

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
                    .addMigrations(MIGRATION_1_2)
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
                    val dao = database.trainingDao()
                    Utils.getDefaultExercisesList(context.resources).forEach {
                        dao.insertExercise(ExerciseEntity(name = it.name, defaultWeight = 0f))
                    }
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            INSTANCE?.let { database ->
                scope.launch {
                }
            }
        }
    }
}