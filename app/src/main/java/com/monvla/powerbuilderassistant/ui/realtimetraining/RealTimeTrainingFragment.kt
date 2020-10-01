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
import com.monvla.powerbuilderassistant.ui.exercise.SetResultDialogFragment
import kotlinx.android.synthetic.main.screen_real_time_training.*
import kotlinx.android.synthetic.main.time_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


//class RealTimeTrainingWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result = coroutineScope {
////        viewModel.timerTick()
//        val viewModel = inputData.get
//        while (!isStopped) {
//            Log.d("LUPA", "TICK")
//            Thread.sleep(1000L)
//        }
//        Result.success()
//    }
//}


class RealTimeTrainingFragment : Screen(), SetResultDialogFragment.SetResultDialogListener {

    companion object {
        const val NOTIFICATION_ID = 1337
        const val CHANNEL_ID = "channel"

        fun getFormattedTime(time: Long): String {
            val timestamp = TimeUnit.SECONDS.toMillis(time)
            val date = Date(timestamp);
            val formatter = SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            // Pass date object
            return formatter.format(date)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel: RealTimeTrainingViewModel by activityViewModels()
    private lateinit var trainingService: RealTimeTrainingService

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    fun addSet() {

//        if (startTime == null) {
//            startTime = System.currentTimeMillis()
//        }

    }

    fun updateTimer(currentTime: Long) {
        val formatted = getFormattedTime(currentTime)
        total_time_counter.text = formatted
        total_time.text = formatted
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = SetResultDialogFragment()
        fragment.listener = this

        viewManager = LinearLayoutManager(context)
        viewAdapter = MyAdapter(this)

        recyclerView = recycler_view_times.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
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
        viewModel._myDataset.observe(viewLifecycleOwner) {
            val i = 0
        }

        viewModel.exercises.observe(this) {
            fragment.exercisesList = it
        }

        increase_counter_button.setOnClickListener {
            viewModel.addSet()
            activity?.let {
                fragment.show(it.supportFragmentManager, "lupa")
            }
        }
        button_start.setOnClickListener {
            real_timer_training_flipper.displayedChild = 1
            viewModel.start()
            startTrainingService()
        }
        stop_counter_button.setOnClickListener {
            real_timer_training_flipper.displayedChild = 2
            viewModel.stopTimer()
            trainingService.stopService()
            viewModel.saveTraining()
        }

        val receiver = TimeReceiver(viewModel)
        context?.registerReceiver(receiver, IntentFilter("GET_CURRENT_TIME")) //<----Register
        viewModel.initialize()
    }

    fun startTrainingService() {
        val sConn = object : ServiceConnection {

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//                Toast.makeText(this@RealTimeTrainingFragment.context, "onServiceConnected", Toast.LENGTH_SHORT).show()

                val binder = service as RealTimeTrainingService.LocalBinder
                trainingService = binder.getService()

                viewModel.currentTime.observe(this@RealTimeTrainingFragment) {
                    updateTimer(it)
                    trainingService.updateNotifactionTime(it)
                }
                viewModel.service = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
//                Toast.makeText(this@RealTimeTrainingFragment.context, "onServiceDisconnected", Toast.LENGTH_SHORT).show()
            }
        }

        Intent(context, RealTimeTrainingService::class.java).also { intent ->
//            context?.startService(intent)
            ContextCompat.startForegroundService(context!!, intent)
            activity!!.bindService(intent, sConn, 0)
        }
    }

    override fun onStart() {
        super.onStart()
//        viewModel.dropData()
    }

    override fun onStop() {
        super.onStop()
//        viewModel.stopTimer()
    }

    class MyAdapter(private val context: RealTimeTrainingFragment) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var myDataset = mutableListOf<RealTimeTrainingViewModel.TrainingSet>()

        class MyViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

        fun setData(data: MutableList<RealTimeTrainingViewModel.TrainingSet>) {
            myDataset = data
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.time_item, parent, false) as ConstraintLayout
            return MyViewHolder(layout)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

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

    override fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<SetResultDialogFragment.SetData>) {
        viewModel.saveSet(data)
    }
}