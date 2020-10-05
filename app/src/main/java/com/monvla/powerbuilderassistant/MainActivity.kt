package com.monvla.powerbuilderassistant

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val SOURCE = "source"
        const val SERVICE = "RealTimeTrainingFragmentService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(select_exercise_toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        select_exercise_toolbar.setupWithNavController(navController, appBarConfiguration)
        if (intent.getStringExtra(SOURCE) == SERVICE) {
            val action = TrainingDairyFragmentDirections.actionScreenTrainingDairyToScreenRealTimeTraining()
            navController.navigate(action)
        }
    }
}
