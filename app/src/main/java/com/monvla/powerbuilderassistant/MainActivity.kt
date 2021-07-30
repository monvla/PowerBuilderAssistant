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
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment.Companion.RTT_IN_PROGRESS
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
        if (isTrainingInProgress()) {
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

    override fun onBackPressed() {
        if (navigationContainer.isBackPressHandleNeeded()) {
            navigationContainer.handleBackPress()
        } else {
            super.onBackPressed()
        }
    }

    override fun isTrainingInProgress() = getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_IN_PROGRESS, false)

    override fun setHomeAsUpEnabled(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
    }

    override fun finishFragment() {
        super.onBackPressed()
    }

    private fun prepareNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController

        navigationContainer = NavigationContainer(this, navController)
        bottom_navigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.screenRealTimeTraining, R.id.trainingDairyFragment, R.id.settingsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
