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
import com.monvla.powerbuilderassistant.statistics.ui.ExerciseStatisticsFragment
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import com.monvla.powerbuilderassistant.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationRoot {

    companion object {
        const val SOURCE = "source"
        const val SERVICE = "RealTimeTrainingFragmentService"
    }

    private lateinit var navController: NavController
    private lateinit var navigationContainer: NavigationContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(select_exercise_toolbar)
        Timber.plant(Timber.DebugTree())

        navigationContainer = NavigationContainer(findNavController(R.id.nav_host_fragment))
        navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        select_exercise_toolbar.setupWithNavController(navController, appBarConfiguration)
        if (isServiceStarted()) {
            navigationContainer.navigateGlobal(RealTimeTrainingFragment::class.java)
        }
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> navigationContainer.navigateGlobal(SettingsFragment::class.java)
                R.id.statistics -> navigationContainer.navigateGlobal(ExerciseStatisticsFragment::class.java)
                R.id.training_dairy -> navigationContainer.navigateGlobal(TrainingDairyFragment::class.java)
            }
            true
        }
    }

    override fun setBottomNavigationVisible(isVisible: Boolean) {
        bottom_navigation.isVisible = isVisible
    }

    override fun navigate(from: Class<*>, to: Class<*>, args: Bundle?) {
        navigationContainer.navigate(from, to, args)
    }

    private fun isServiceStarted() = getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_SERVICE_STARTED, false)
}
