package com.monvla.powerbuilderassistant

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.Companion.RTT_SERVICE_STARTED
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationRoot {

    companion object {
        const val SOURCE = "source"
        const val SERVICE = "RealTimeTrainingFragmentService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContentView(R.layout.activity_main)
        setSupportActionBar(select_exercise_toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        select_exercise_toolbar.setupWithNavController(navController, appBarConfiguration)
        val serviceStarted = getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_SERVICE_STARTED, false)
        if (serviceStarted) {
            val action = TrainingDairyFragmentDirections.actionTrainingDairyFragmentToScreenRealTimeTraining()
            navController.navigate(action)
        }
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> navController.navigate(R.id.action_global_exercisesListFragment)
                R.id.statistics -> navController.navigate(R.id.action_global_exerciseStatisticsFragment)
                R.id.training_dairy -> navController.navigate(R.id.action_global_screenTrainingDairy)
            }
            true
        }
    }

    override fun setBottomNavigationVisible(isVisible: Boolean) {
        bottom_navigation.isVisible = isVisible
    }
}
