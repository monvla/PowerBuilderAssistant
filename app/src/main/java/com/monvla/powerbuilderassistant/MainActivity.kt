package com.monvla.powerbuilderassistant

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.Companion.RTT_SERVICE_STARTED
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationRoot {

    companion object {
        const val SOURCE = "source"
        const val SERVICE = "RealTimeTrainingFragmentService"
    }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navigationContainer: NavigationContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        prepareNavigation()
        if (isServiceStarted()) {
            navigationContainer.navigateGlobal(RealTimeTrainingFragment::class.java)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun setBottomNavigationVisible(isVisible: Boolean) {
        bottom_navigation.isVisible = isVisible
    }

    override fun navigate(from: Class<*>, to: Class<*>, args: Bundle?) {
        navigationContainer.navigate(from, to, args)
    }

    private fun isServiceStarted() = getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_SERVICE_STARTED, false)

    private fun prepareNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController

        navigationContainer = NavigationContainer(navController)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.trainingDairyFragment, R.id.settingsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
