package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingFragment
import com.monvla.powerbuilderassistant.ui.realtimetraining.RealTimeTrainingViewModel
import kotlinx.android.synthetic.main.time_item.view.*

class RTTFinishedSetsAdapter(private val context: RealTimeTrainingFragment) :
    RecyclerView.Adapter<RTTFinishedSetsAdapter.RTTFinishedSetsHolder>() {

    var myDataset = mutableListOf<RealTimeTrainingViewModel.TrainingSet>()

    class RTTFinishedSetsHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

    fun setData(data: MutableList<RealTimeTrainingViewModel.TrainingSet>) {
        myDataset = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RTTFinishedSetsHolder {
        val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.time_item, parent, false) as ConstraintLayout
        return RTTFinishedSetsHolder(layout)
    }

    override fun onBindViewHolder(holder: RTTFinishedSetsHolder, position: Int) {
        holder.layout.set_num.text = myDataset[position]?.number.toString()
        holder.layout.set_time.text = Utils.getFormattedTimeFromSeconds(myDataset[position].time)
    }

    override fun getItemCount() = myDataset.size
}