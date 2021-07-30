package com.monvla.powerbuilderassistant

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import com.monvla.powerbuilderassistant.ui.record.TrainingDetailsFragment
import com.monvla.powerbuilderassistant.ui.settings.ExerciseEditFragment
import com.monvla.powerbuilderassistant.ui.settings.ExercisesListFragment
import com.monvla.powerbuilderassistant.ui.settings.SettingsFragment

class NavigationContainer(
    private val context: Context,
    private val navigationController: NavController
) {

    private val globalDestinations = listOf(
        GlobalDestination(
            RealTimeTrainingFragment::class.java,
            R.id.action_global_screenRealTimeTraining,
            R.id.trainingDairyFragment
        )
    )

    private val restrictedBackNavigationFragmentsList = listOf(
        TrainingSetResultFragment::class.java.name,
        RealTimeTrainingFragment::class.java.name
    )

    private val fragmentDestinations = listOf(
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
        globalDestinations.forEach {
            if (it.javaClass == className) {
                val navOptions = NavOptions.Builder().setPopUpTo(it.popUpFragmentId, true).build()
                navigationController.navigate(it.actionId, null, navOptions)
            }
        }
    }

    fun handleBackPress() {

    }

    fun isBackPressHandleNeeded(): Boolean {
        val destinationClassName =
            (navigationController.currentBackStackEntry?.destination as FragmentNavigator.Destination).className
        val isRestrictedFragment = restrictedBackNavigationFragmentsList.contains(destinationClassName)
        return isRestrictedFragment && (context as MainActivity).isTrainingInProgress()
    }

}

data class GlobalDestination(
    val javaClass: Class<*>,
    @IdRes val actionId: Int,
    @IdRes val popUpFragmentId: Int
)

data class FragmentDestination(
    val fragmentFrom: Class<*>,
    val fragmentTo: Class<*>,
    @IdRes val action: Int
)