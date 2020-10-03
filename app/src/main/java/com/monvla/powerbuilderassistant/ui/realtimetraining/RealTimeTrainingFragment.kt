package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils.Companion.getFormattedTimeFromSeconds
import com.monvla.powerbuilderassistant.adapters.RTTFinishedSetsAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel.Companion.OPEN_FROM_FRAGMENT
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel.Companion.OPEN_FROM_SERVICE
import kotlinx.android.synthetic.main.screen_real_time_training.*
import kotlinx.android.synthetic.main.time_item.view.*


class RealTimeTrainingFragment : Screen(), SetResultDialogFragment.SetResultDialogListener {

    companion object {
        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "channel"

    }

    private val viewModel: RealTimeTrainingViewModel by activityViewModels()
    private lateinit var trainingService: RealTimeTrainingService
    lateinit var receiver: TimeReceiver

    val args: RealTimeTrainingFragmentArgs by navArgs()

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTimeFromSeconds(currentTime)
        total_time_counter.text = formatted
        total_time.text = formatted
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        real_timer_training_flipper.displayedChild = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = RTTFinishedSetsAdapter(this)

        recyclerSets.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        viewModel.myDataset.observe(viewLifecycleOwner) {
            viewAdapter.setData(it)
            viewAdapter.notifyDataSetChanged()
        }
        viewModel.setsCounter.observe(this) {
            sets_counter.apply {
                text = it.toString()
            }
            total_sets.text = it.toString()
        }

        increase_counter_button.setOnClickListener {
            showSetExercisesDialog()
            trainingService.pause()
        }
        button_start.setOnClickListener {
            real_timer_training_flipper.displayedChild = 1
            viewModel.start()
            startTrainingService()
            viewModel.trainingFinished = false
        }
        stop_counter_button.setOnClickListener {
            real_timer_training_flipper.displayedChild = 2
            viewModel.stopTimer()
            trainingService.stopService()
            showSetExercisesDialog()
            viewModel.trainingFinished = true
        }

        receiver = TimeReceiver(viewModel)
        context?.registerReceiver(receiver, IntentFilter("GET_CURRENT_TIME")) //<----Register

        when(args.launchSourceId) {
            OPEN_FROM_SERVICE -> {
                real_timer_training_flipper.displayedChild = 1
            }
            OPEN_FROM_FRAGMENT -> {
                viewModel.initialize()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
    }

    fun showSetExercisesDialog() {
        activity?.let {
            val fragment = SetResultDialogFragment(viewModel.getLoadedExercises())
            fragment.listener = this
            fragment.show(it.supportFragmentManager, fragment.javaClass.simpleName)
        }
    }

    fun startTrainingService() {
        val sConn = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

                val binder = service as RealTimeTrainingService.LocalBinder
                trainingService = binder.getService()

                viewModel.currentTime.observe(this@RealTimeTrainingFragment) {
                    updateTimer(it)
                    trainingService.updateNotifactionTime(it)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        Intent(context, RealTimeTrainingService::class.java).also { intent ->
            ContextCompat.startForegroundService(context!!, intent)
            activity!!.bindService(intent, sConn, 0)
        }
    }

    class TimeReceiver(val viewModel: RealTimeTrainingViewModel) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == "GET_CURRENT_TIME") {
                val time = intent.getLongExtra("TIME", 0)
                viewModel.updateTimer(time)
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<SetResultDialogFragment.TrainingSetData>) {
        viewModel.saveSet(data)
        trainingService.unpause()
        if (viewModel.trainingFinished) {
            viewModel.saveTraining()
        } else {
            viewModel.addSet()
        }
    }
}