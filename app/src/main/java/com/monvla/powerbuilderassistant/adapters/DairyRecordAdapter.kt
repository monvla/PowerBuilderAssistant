package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.android.synthetic.main.item_dairy_record.view.*
import java.text.SimpleDateFormat
import java.util.*

class DairyRecordAdapter(private var data: List<TrainingRecordEntity>) :
    RecyclerView.Adapter<DairyRecordAdapter.DairyRecordViewHolder>() {

    var callback : ItemClick? = null

    interface ItemClick {
        fun onItemClicked(exercise: TrainingRecordEntity)
    }

    class DairyRecordViewHolder(val container: LinearLayout) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): DairyRecordViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dairy_record, parent, false) as LinearLayout

        return DairyRecordViewHolder(textView)
    }

    override fun onBindViewHolder(holder: DairyRecordViewHolder, position: Int) {
        holder.container.date.text = "Тренировка ${Utils.getFormattedDateTime(data[position].date)}"//"LUPA id: ${data[position].id}"
        holder.container.length.text = "Длительность ${Utils.getFormattedTimeFromSeconds(data[position].length)}"//"LUPA id: ${data[position].id}"
        holder.container.setOnClickListener {
            callback?.onItemClicked(data[position])
        }
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<TrainingRecordEntity>) {
        data = newData
        notifyDataSetChanged()
    }
}