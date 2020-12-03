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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils.Companion.getFormattedTimeFromSeconds
import com.monvla.powerbuilderassistant.adapters.RTTFinishedSetsAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingService.Companion.TRAINING_STATUS
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel.State
import kotlinx.android.synthetic.main.screen_real_time_training.*


class RealTimeTrainingFragment : Screen(), TrainingServiceListener {

    companion object {
        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "channel"

        const val DISPLAYED_CHILD_FINISHED = 2
        const val DISPLAYED_CHILD_IN_PROGRESS = 1
        const val DISPLAYED_CHILD_READY = 0
    }

    private val viewModel: RealTimeTrainingViewModel by activityViewModels()
    var trainingService: RealTimeTrainingService? = null
    lateinit var receiver: TrainingStatusReceiver

    init {
        screenLayout = R.layout.screen_real_time_training
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
            viewModel.addSet()
            trainingService?.pause()
        }
        button_start.setOnClickListener {
            showTimer()
            viewModel.start()
            trainingService?.unpause()
            startTrainingService()
        }
        stop_counter_button.setOnClickListener {
            stopTrainingDialog()
        }

        receiver = TrainingStatusReceiver(this)
        context?.registerReceiver(receiver, IntentFilter(TRAINING_STATUS))
        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                is State.Ready -> initialize()
                is State.InProgress -> showTimer()
                is State.Update -> {
                    trainingService?.unpause()
                    updateTimer(it.time)
                    current_set_text.text = getString(R.string.rtt_current_set, it.sets)
                    viewAdapter.setData(it.trainingSets)
                    viewAdapter.notifyDataSetChanged()
                    showTimer()
                }
                is State.FillSet -> {
                    val action = RealTimeTrainingFragmentDirections.actionScreenRealTimeTrainingToExerciseSetResultFragment(
                        trainingId = it.trainingId,
                        setId = it.setId,
                        isNewSet = true
                    )
                    this.findNavController().navigate(action)
                }
                is State.Finished -> {
                    updateTimer(it.totalTime)
                    total_sets_done.text = getString(R.string.rtt_sets_done, it.setsCounter)
                    real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_FINISHED
                }
            }
        }
        viewModel.viewCreated()
        connectToService()
    }

    private fun stopTrainingDialog() =
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.rtt_stop_training_dialog))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    trainingService?.stopService()
                    viewModel.timerStopped()
                    viewModel.addSet()
                }
                .setNegativeButton(android.R.string.no, null)
                .show();

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
    }

    override fun onTick(time: Long) {
        viewModel.timerTick(time)
    }

    private fun connectToService() {
        val sConn = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RealTimeTrainingService.LocalBinder
                trainingService = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        val intent = Intent(context, RealTimeTrainingService::class.java)
        requireActivity().bindService(intent, sConn, 0)
    }

    private fun startTrainingService() {
        val intent = Intent(context, RealTimeTrainingService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTimeFromSeconds(currentTime)
        total_time_counter.text = formatted
        total_time.text = resources.getString(R.string.rtt_total_time, formatted)
    }

    private fun showTimer() {
        if (real_timer_training_flipper.displayedChild != DISPLAYED_CHILD_IN_PROGRESS) {
            real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_IN_PROGRESS
        }
    }

    private fun initialize() {
        real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_READY
    }

    class TrainingStatusReceiver(private val listener: TrainingServiceListener) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == TRAINING_STATUS) {
                val time = intent.getLongExtra(RealTimeTrainingService.TIME_ARG, 0)
                listener.onTick(time)
            }
        }
    }
}