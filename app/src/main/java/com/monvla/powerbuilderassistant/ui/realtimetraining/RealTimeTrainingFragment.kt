package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_real_time_training.*
import kotlinx.android.synthetic.main.time_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class RealTimeTrainingFragment : Screen(), SetResultDialogFragment.SetResultDialogListener {

    companion object {
        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "channel"

        fun getFormattedTime(time: Long): String {
            val timestamp = TimeUnit.SECONDS.toMillis(time)
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(date)
        }
    }

    private val viewModel: RealTimeTrainingViewModel by activityViewModels()
    private lateinit var trainingService: RealTimeTrainingService

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTime(currentTime)
        total_time_counter.text = formatted
        total_time.text = formatted
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAdapter = FinishedSetsAdapter(this)

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
            viewModel.addSet()
            showSetExercisesDialog()
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
            viewModel.addSet()
            viewModel.trainingFinished = true
        }

        val receiver = TimeReceiver(viewModel)
        context?.registerReceiver(receiver, IntentFilter("GET_CURRENT_TIME")) //<----Register
        viewModel.initialize()
    }

    fun showSetExercisesDialog() {
        activity?.let {
            val fragment = SetResultDialogFragment(viewModel.getLoadedExercises())
            fragment.listener = this
            fragment.show(it.supportFragmentManager, "lupa")
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
                viewModel.service = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }

        Intent(context, RealTimeTrainingService::class.java).also { intent ->
            ContextCompat.startForegroundService(context!!, intent)
            activity!!.bindService(intent, sConn, 0)
        }
    }

    class FinishedSetsAdapter(private val context: RealTimeTrainingFragment) :
        RecyclerView.Adapter<FinishedSetsAdapter.FinishedSetsHolder>() {

        var myDataset = mutableListOf<RealTimeTrainingViewModel.TrainingSet>()

        class FinishedSetsHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

        fun setData(data: MutableList<RealTimeTrainingViewModel.TrainingSet>) {
            myDataset = data
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FinishedSetsHolder {
            val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.time_item, parent, false) as ConstraintLayout
            return FinishedSetsHolder(layout)
        }

        override fun onBindViewHolder(holder: FinishedSetsHolder, position: Int) {

            val timestamp = TimeUnit.SECONDS.toMillis(myDataset[position]!!.time)
            val date = Date(timestamp)
            val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
            val dateFormatted: String = formatter.format(date)

            holder.layout.set_num.text = myDataset[position]?.number.toString()
            holder.layout.set_time.text = dateFormatted
        }

        override fun getItemCount() = myDataset.size
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
        if (viewModel.trainingFinished) {
            viewModel.saveTraining()
        }
    }
}