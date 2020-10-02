package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetEntity
import com.monvla.powerbuilderassistant.vo.SetExerciseEntity
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.coroutines.launch
import java.util.*

class RealTimeTrainingViewModel(application: Application) : AndroidViewModel(application) {

    data class TrainingSet(val number: Int, val time: Long)

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(application, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    var trainingFinished: Boolean = false

    private var mytimer: Timer? = null
    private val workManager = WorkManager.getInstance()

    private var _currentTime = MutableLiveData(0L)
    var currentTime = _currentTime as LiveData<Long>
    private var _setsCounter = MutableLiveData(0)
    var setsCounter = _setsCounter as LiveData<*>

    val _myDataset = MutableLiveData(mutableListOf<TrainingSet>())
    val myDataset: LiveData<MutableList<TrainingSet>> = _myDataset

    var service: RealTimeTrainingService? = null
    private val _exercises = MutableLiveData<List<ExerciseEntity>>()
    val exercises = _exercises as LiveData<List<ExerciseEntity>>
    var nextTrainingId: Long? = null

    fun updateTimer(time: Long) {
        _currentTime.value = time
    }

    fun dropData() {
        _myDataset.value?.clear()
        _setsCounter.value = 0
        _currentTime.value = 0
    }

    fun stopTimer() {
        mytimer?.cancel()
        mytimer = null
    }

    fun initialize() {
        viewModelScope.launch {
            _exercises.value = repository.getAllExercises()
            nextTrainingId = (repository.getLastTrainingRowId() ?: 0) + 1
        }
    }

    fun start() {
        dropData()
        addSet()
    }

    fun addSet() {
        _setsCounter.value = _setsCounter.value?.plus(1)
        _myDataset.value?.add(TrainingSet(_setsCounter.value!!, _currentTime.value!!))
        _myDataset.value = _myDataset.value
    }

    fun saveTraining() {
        val trainingLength = _currentTime.value
        trainingLength?.let {
            viewModelScope.launch {
                repository.insertTrainingRecord(TrainingRecordEntity(date = System.currentTimeMillis(), length = it))
            }
        }
    }

    fun saveSet(data: List<SetResultDialogFragment.TrainingSetData>) {
        viewModelScope.launch {
            nextTrainingId?.let {trainingId ->
                val setId = repository.insertSet(SetEntity(trainingRecordId = trainingId, number = _setsCounter.value!!))
                data.forEach {
                    val exercise = repository.getExerciseByName(it.name)
                    repository.insertSetExercise(SetExerciseEntity(setId = setId, weight = it.weight, repeats = it.repeats, exerciseId = exercise.id))
                }
            }
        }
    }

    fun getLoadedExercises() = _exercises.value!!


}