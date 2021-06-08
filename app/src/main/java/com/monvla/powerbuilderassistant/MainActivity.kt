package com.monvla.powerbuilderassistant

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.Companion.RTT_SERVICE_STARTED
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationRoot {

    companion object {
        const val SOURCE = "source"
        const val SERVICE = "RealTimeTrainingFragmentService"
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(select_exercise_toolbar)
        Timber.plant(Timber.DebugTree())

        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        select_exercise_toolbar.setupWithNavController(navController, appBarConfiguration)
        if (isServiceStarted()) {
            val action = TrainingDairyFragmentDirections.actionGlobalScreenRealTimeTraining()
            navController.navigate(action)
        }
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> navController.navigate(R.id.action_global_settingsFragment)
                R.id.statistics -> navController.navigate(R.id.action_global_exerciseStatisticsFragment)
                R.id.training_dairy -> navController.navigate(R.id.action_global_screenTrainingDairy)
            }
            true
        }
    }

    override fun setBottomNavigationVisible(isVisible: Boolean) {
        bottom_navigation.isVisible = isVisible
    }

    private fun isServiceStarted() = getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_SERVICE_STARTED, false)
}
