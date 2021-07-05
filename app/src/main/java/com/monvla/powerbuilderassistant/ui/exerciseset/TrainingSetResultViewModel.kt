package com.monvla.powerbuilderassistant.ui.exerciseset

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.*
import com.monvla.powerbuilderassistant.db.TrainingRoomDb
import com.monvla.powerbuilderassistant.repository.TrainingRepository
import com.monvla.powerbuilderassistant.vo.*
import java.util.*
import kotlinx.android.parcel.Parcelize

class TrainingSetResultViewModel(
    context: Context,
    private val setId: Long,
    private val setNumber: Int,
    var setExercisesList: SetExercisesList
) : ViewModel() {

    companion object {
    }

    @Parcelize
    data class FragmentResult(val setId: Long, val setNumber: Int, val setExercisesList: SetExercisesList) : Parcelable

    data class DialogData(val exercisesList: List<ExerciseEntity>, val setExercise: SetExercise?)

    data class TrainingSetData(val setNumber: Int, val setExercises: List<SetExercise>)

    private val repository: TrainingRepository

    init {
        val dao = TrainingRoomDb.getDatabase(context, viewModelScope).trainingDao()
        repository = TrainingRepository(dao)
    }

    val showAddExerciseDialog = MediatorLiveData<DialogData>()

    private lateinit var exercises: List<ExerciseEntity>

    private val _setData = liveData {
        if (isEditSetMode()) {
            val setExercisesListTemp = SetExercisesList()
            val setExerciseEntities = repository.getSetExercisesBySetId(setId)
            setExerciseEntities.forEach {
                val exercise = repository.getExerciseById(it.exerciseId)
                setExercisesListTemp.add(SetExercise.getSetExerciseFromEntity(exercise, it))
            }
            setExercisesList = setExercisesListTemp
        } else {
            prepareNewExerciseDialog(null)
        }
        emit(TrainingSetData(setNumber, setExercisesList))
    } as MutableLiveData
    val setData = _setData as LiveData<TrainingSetData>

    private val _deleteTrigger = MutableLiveData<Unit>()
    val deleteTrigger = _deleteTrigger as LiveData<Unit>

    fun exerciseUpdated(setExercise: SetExercise) {
        var updated = false
        setExercisesList.forEachIndexed { index, oldExercise ->
            if (oldExercise.uuid == setExercise.uuid) {
                setExercisesList[index] = setExercise
                updated = true
            }
        }
        if (!updated) {
            setExercisesList.add(setExercise)
        }
        if (isEditSetMode()) {
            SetExerciseEntity(
                setId = setExercise.setId,
                exerciseId = setExercise.exerciseId,
                weight = setExercise.weight,
                repeats = setExercise.repeats
            )
        }
        _setData.value = TrainingSetData(setNumber, setExercisesList)
    }

    fun prepareNewExerciseDialog(exercise: SetExercise?) {
        showAddExerciseDialog.addSource(repository.getAllExercises()) {
            showAddExerciseDialog.value = DialogData(it, exercise)
            exercises = it
        }
    }

    fun deleteSetExerciseRequested(exercise: SetExercise) {
        var removeIndex: Int? = null
        setExercisesList.forEachIndexed { index, oldExercise ->
            if (oldExercise.uuid == exercise.uuid) {
                removeIndex = index
                return@forEachIndexed
            }
        }
        removeIndex?.let {
            setExercisesList.removeAt(it)
            _setData.value = TrainingSetData(setNumber, setExercisesList)
        }
    }

    fun getSetExercises() = setExercisesList

    private fun isEditSetMode() = setId != UNDEFINED_ID
}