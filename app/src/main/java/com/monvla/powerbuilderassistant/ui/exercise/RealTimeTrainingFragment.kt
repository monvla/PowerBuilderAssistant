package com.monvla.powerbuilderassistant.ui.exercise

import android.os.Bundle
import android.util.StatsLog.logEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlin.collections.ArrayList


class RealTimeTrainingFragment : Screen() {

    data class Set(val number: Int, val time: Long)

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var startTime: Long? = null

    lateinit var myDataset: ArrayList<Set?>

    init {
        screenLayout = R.layout.screen_real_time_training
    }

    fun addSet() {

        if (startTime == null) {
            startTime = System.currentTimeMillis()
        }

        val currentTime = System.currentTimeMillis()
        sets_counter.apply {
            val value = Integer.parseInt(text.toString()) + 1
            text = value.toString()
            myDataset.add(Set(value, currentTime))
        }

        val totalTime = myDataset[myDataset.size-1]!!.time - startTime!!

        val date = Date(totalTime);
        val formatter = SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // Pass date object
        val formatted = formatter.format(date);

        total_time_counter.text = formatted
        viewAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myDataset = arrayListOf()

        viewManager = LinearLayoutManager(context)
        viewAdapter = MyAdapter(myDataset)

        recyclerView = recycler_view_times.apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = viewAdapter

        }

        increase_counter_button.setOnClickListener {
            addSet()

        }

    }

    class MyAdapter(private val myDataset: ArrayList<Set?>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)


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

            val date = Date(myDataset[position]!!.time)
            val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
            val dateFormatted: String = formatter.format(date)

            holder.layout.set_num.text = myDataset[position]?.number.toString()
            holder.layout.set_time.text = dateFormatted
        }

        override fun getItemCount() = myDataset.size
    }
}