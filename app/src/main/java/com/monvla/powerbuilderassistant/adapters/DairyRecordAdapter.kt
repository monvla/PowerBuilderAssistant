package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.vo.TrainingRecord
import kotlinx.android.synthetic.main.item_dairy_record.view.*

class DairyRecordAdapter(private val dataset: ArrayList<TrainingRecord>) :
    RecyclerView.Adapter<DairyRecordAdapter.DairyRecordViewHolder>() {

    var callback : ItemClick? = null

    interface ItemClick {
        fun onExerciseItemClicked(exercise: TrainingRecord)
    }

    class DairyRecordViewHolder(val container: LinearLayout) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DairyRecordViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dairy_record, parent, false) as LinearLayout
        return DairyRecordViewHolder(textView)
    }

    override fun onBindViewHolder(holder: DairyRecordViewHolder, position: Int) {
        holder.container.date.text = "LUPA"
    }

    override fun getItemCount() = dataset.size
}