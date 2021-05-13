package com.monvla.powerbuilderassistant.ui.exerciseset

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monvla.powerbuilderassistant.vo.SetExercisesList

class TrainingSetResultViewModelFactory(
    private val context: Context,
    private val setId: Long,
    private val setNumber: Int,
    private val setExercisesList: SetExercisesList) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingSetResultViewModel(context, setId, setNumber, setExercisesList) as T
    }
}