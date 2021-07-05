package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils.Companion.getFormattedTimeFromSeconds
import com.monvla.powerbuilderassistant.adapters.RTTFinishedSetsAdapter
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.Companion.RTT_SERVICE_STARTED
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.Companion.TRAINING_STATUS
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.LocalBinder
import com.monvla.powerbuilderassistant.service.RealTimeTrainingService.ServiceConnection
import com.monvla.powerbuilderassistant.service.TrainingService
import com.monvla.powerbuilderassistant.service.TrainingServiceListener
import com.monvla.powerbuilderassistant.ui.SimpleFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel.State
import com.monvla.powerbuilderassistant.vo.SetExercisesList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.screen_real_time_training.*
import timber.log.Timber

class RealTimeTrainingFragment : SimpleFragment(), TrainingServiceListener {

    companion object {
        const val CHANNEL_ID = "channel"

        const val DISPLAYED_CHILD_FINISHED = 2
        const val DISPLAYED_CHILD_IN_PROGRESS = 1
        const val DISPLAYED_CHILD_READY = 0
    }

    private val viewModel: RealTimeTrainingViewModel by viewModels()

    var trainingService: TrainingService? = null
    private var receiver: TrainingStatusReceiver? = null
    private var serviceConnection: ServiceConnection? = null

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectToService()
        val viewAdapter = RTTFinishedSetsAdapter()

        recyclerSets.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
        add_set_button.setOnClickListener {
            viewModel.newSetClicked()
        }
        button_start.setOnClickListener {
            displayTimer()
            viewModel.trainingStarted()
            startTrainingService()
        }
        stop_counter_button.setOnClickListener {
            stopTrainingDialog()
        }

        viewModel.navigateToSetResultFragment.subscribeChanges(viewLifecycleOwner) { navigationInfo ->
            val args = Bundle().also {
                it.putLong(TrainingSetResultFragment.KEY_SET_ID, -1)
                it.putInt(TrainingSetResultFragment.KEY_SET_NUMBER, navigationInfo.currentSetNumber)
                it.putParcelable(TrainingSetResultFragment.KEY_SET_EXERCISES, SetExercisesList())
            }
            navigationRoot.navigate(this.javaClass, TrainingSetResultFragment::class.java, args)
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when(it) {
                is State.Ready -> {
                    real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_READY
                }
                is State.Update -> {
                    updateTimer(it.time)
                    current_set_text.text = getString(R.string.rtt_current_set, it.sets)
                    viewAdapter.setData(it.trainingSets)
                    viewAdapter.notifyDataSetChanged()
                    displayTimer()
                    trainingService?.setCachedTrainingSets(it.trainingSets)
                }
                is State.Finished -> {
                    updateTimer(it.totalTime)
                    total_sets_done.text = getString(R.string.rtt_sets_done, it.setsCounter)
                    real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_FINISHED
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setFragmentResultListener(TrainingSetResultFragment.KEY_TRAINING_SET_RESULT) { _, bundle ->
            val setExercisesList = bundle.getParcelable<SetExercisesList>(TrainingSetResultFragment.KEY_SET_EXERCISES)
            viewModel.addSet(checkNotNull(setExercisesList))
        }
        receiver = TrainingStatusReceiver {
            viewModel.timerTick(it)
        }
        requireContext().registerReceiver(receiver, IntentFilter(TRAINING_STATUS))
    }

    override fun onStop() {
        super.onStop()
        receiver?.let {
            Timber.d("unregisterReceiver")
            requireContext().unregisterReceiver(receiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let {
            Timber.d("unbindService")
            requireActivity().unbindService(it)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        displayTimer()
    }

    private fun setServiceRunningState(value: Boolean) {
        Timber.d("set service running state: $value")
        val preferences = requireActivity().getPreferences(Service.MODE_PRIVATE)
        with(preferences.edit()) {
            putBoolean(RTT_SERVICE_STARTED, value)
            apply()
        }
        Timber.d("set result: ${requireActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(RTT_SERVICE_STARTED, false)}")
    }

    override fun onStateRecieved(value: Boolean) {
        setServiceRunningState(value)
    }

    private fun stopTrainingDialog() =
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.rtt_stop_training_dialog))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    trainingService?.stop()
                    viewModel.trainingFinished()
                }
                .setNegativeButton(android.R.string.no, null)
                .show();

    private fun connectToService() {
        Timber.d("connectToService")
        serviceConnection = object : ServiceConnection() {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as LocalBinder
                trainingService = binder.getService().also {
                    it.getCachedData().also { data ->
                        if (!data.cachedTrainingSets.isNullOrEmpty()) {
                            viewModel.updateTrainingSets(data.cachedTrainingSets)
                        }
                        if (data.timeStarted != 0L) {
                            viewModel.updateTrainingStartTimestamp(data.timeStarted)
                        }
                    }
                    it.registerServiceListener(this@RealTimeTrainingFragment)
                }
            }
        }

        serviceConnection?.let { serviceConnection ->
            Intent(context, RealTimeTrainingService::class.java).also { intent ->
                requireActivity().bindService(intent, serviceConnection, 0)
            }
        }
    }

    private fun startTrainingService() {
        Intent(context, RealTimeTrainingService::class.java).also {
            ContextCompat.startForegroundService(requireContext(), it)
        }
        setServiceRunningState(true)
    }

    private fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTimeFromSeconds(currentTime)
        total_time_counter.text = formatted
        total_time.text = resources.getString(R.string.rtt_total_time, formatted)
    }

    private fun displayTimer() {
        if (real_timer_training_flipper.displayedChild != DISPLAYED_CHILD_IN_PROGRESS) {
            real_timer_training_flipper.displayedChild = DISPLAYED_CHILD_IN_PROGRESS
        }
    }

    class TrainingStatusReceiver(private val onTick: (time: Long) -> Unit) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == TRAINING_STATUS) {
                val time = intent.getLongExtra(RealTimeTrainingService.TIME_ARG, 0)
                onTick(time)
            }
        }
    }
}