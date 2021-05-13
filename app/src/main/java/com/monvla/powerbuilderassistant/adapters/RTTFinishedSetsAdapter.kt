package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.vo.ServiceTrainingSet
import kotlinx.android.synthetic.main.time_item.view.*

class RTTFinishedSetsAdapter : RecyclerView.Adapter<RTTFinishedSetsAdapter.RTTFinishedSetsHolder>() {

    private var trainingSets = mutableListOf<ServiceTrainingSet>()

    class RTTFinishedSetsHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout)

    fun setData(data: MutableList<ServiceTrainingSet>) {
        trainingSets = data
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
        holder.layout.set_num.text = (position + 1).toString()
        holder.layout.set_time.text = Utils.getFormattedTimeFromSeconds(trainingSets[position].time)
    }

    override fun getItemCount() = trainingSets.size
}