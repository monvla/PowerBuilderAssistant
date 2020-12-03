package com.monvla.powerbuilderassistant.ui.exerciseset

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrainingSetResultViewModelFactory(val application: Application, val setId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingSetResultViewModel(application, setId) as T
    }
}