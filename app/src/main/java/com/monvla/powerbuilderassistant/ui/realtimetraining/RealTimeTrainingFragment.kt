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
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils.Companion.getFormattedTimeFromSeconds
import com.monvla.powerbuilderassistant.adapters.RTTFinishedSetsAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingService.Companion.TIME_ARG
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingService.Companion.TRAINING_STATUS
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel.State
import kotlinx.android.synthetic.main.screen_real_time_training.*


class RealTimeTrainingFragment : Screen(), SetResultDialogFragment.SetResultDialogListener {

    companion object {
        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "channel"

    }

    private val viewModel: RealTimeTrainingViewModel by activityViewModels()
    private var trainingService: RealTimeTrainingService? = null
    lateinit var receiver: TrainingStatusReceiver

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTimeFromSeconds(currentTime)
        total_time_counter.text = formatted
        total_time.text = formatted
    }

    fun showTimer() {
        if (real_timer_training_flipper.displayedChild != 1) {
            real_timer_training_flipper.displayedChild = 1
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        showTimer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = RTTFinishedSetsAdapter(this)

        recyclerSets.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
        increase_counter_button.setOnClickListener {
            showSetExercisesDialog()
            trainingService?.pause()
        }
        button_start.setOnClickListener {
            showTimer()
            viewModel.start()
            startTrainingService()
        }
        stop_counter_button.setOnClickListener {
            showSetExercisesDialog()
            trainingService?.stopService()
            viewModel.timerStopped()
        }

        receiver = TrainingStatusReceiver(viewModel)
        context?.registerReceiver(receiver, IntentFilter(TRAINING_STATUS))

        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                is State.Ready -> viewModel.initialize()
                is State.InProgress -> showTimer()
                is State.Update -> {
                    updateTimer(it.time)
                    sets_counter.text = it.sets.toString()
                    total_sets.text = it.sets.toString()
                    viewAdapter.setData(it.trainingSets)
                    viewAdapter.notifyDataSetChanged()
                    showTimer()
                }
                is State.Finished -> {
                    real_timer_training_flipper.displayedChild = 2
                }
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
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        Intent(context, RealTimeTrainingService::class.java).also { intent ->
            ContextCompat.startForegroundService(context!!, intent)
            activity!!.bindService(intent, sConn, 0)
        }
    }

    class TrainingStatusReceiver(val viewModel: RealTimeTrainingViewModel) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == TRAINING_STATUS) {
                val time = intent.getLongExtra(TIME_ARG, 0)
                viewModel.timerTick(time)
            }
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<SetResultDialogFragment.SetExercise>) {
        if (viewModel.isTimerStopped) {
            viewModel.trainingDone(data)
        } else {
            trainingService?.unpause()
            viewModel.trainingSetDone(data)
        }
    }
}