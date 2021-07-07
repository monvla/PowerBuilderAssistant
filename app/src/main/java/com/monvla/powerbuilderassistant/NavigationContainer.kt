package com.monvla.powerbuilderassistant

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import com.monvla.powerbuilderassistant.ui.record.TrainingDetailsFragment
import com.monvla.powerbuilderassistant.ui.settings.ExerciseEditFragment
import com.monvla.powerbuilderassistant.ui.settings.ExercisesListFragment
import com.monvla.powerbuilderassistant.ui.settings.SettingsFragment

class NavigationContainer(private val navigationController: NavController) {

    private val globalDestinations = mapOf<Class<*>, Int>(
        RealTimeTrainingFragment::class.java to R.id.action_global_screenRealTimeTraining,
    )

    data class FragmentDestination(val fragmentFrom: Class<*>, val fragmentTo: Class<*>, @IdRes val action: Int)

    private val fragmentDestinations = listOf(
        FragmentDestination(
            TrainingDairyFragment::class.java,
            RealTimeTrainingFragment::class.java,
            R.id.action_trainingDairyFragment_to_screenRealTimeTraining
        ),
        FragmentDestination(
            TrainingDairyFragment::class.java,
            TrainingDetailsFragment::class.java,
            R.id.action_trainingDairyFragment_to_trainingDetailsFragment
        ),
        FragmentDestination(
            TrainingDetailsFragment::class.java,
            TrainingSetResultFragment::class.java,
            R.id.action_trainingDetailsFragment_to_exerciseSetResultFragment
        ),
        FragmentDestination(
            RealTimeTrainingFragment::class.java,
            TrainingSetResultFragment::class.java,
            R.id.action_screenRealTimeTraining_to_exerciseSetResultFragment
        ),
        FragmentDestination(
            SettingsFragment::class.java,
            ExercisesListFragment::class.java,
            R.id.action_settingsFragment_to_exercisesListFragment
        ),
        FragmentDestination(
            ExercisesListFragment::class.java,
            ExerciseEditFragment::class.java,
            R.id.action_exercisesListFragment_to_exerciseEditFragment
        )
    )

    fun navigate(from: Class<*>, to: Class<*>, args: Bundle?) {
        fragmentDestinations.forEach {
            if (it.fragmentFrom == from && it.fragmentTo == to) {
                navigationController.navigate(it.action, args)
            }
        }
    }

    fun navigateGlobal(className: Class<*>) {
        globalDestinations.entries.forEach {
            if (it.key == className) navigationController.navigate(it.value)
        }
    }

}

