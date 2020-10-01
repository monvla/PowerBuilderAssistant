package com.monvla.powerbuilderassistant.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.item_exercise.view.*

class DisplayableDairyRecordAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<DisplayableDairyRecordAdapter.RecordViewHolder>() {

    var callback : ItemClick? = null

    private var data = emptyList<ExerciseEntity>()

    interface ItemClick {
        fun onRecordItemClicked(exerciseEntity: ExerciseEntity)
    }

    class RecordViewHolder(val container: LinearLayout) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecordViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false) as LinearLayout
        return RecordViewHolder(textView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = data[position]
        holder.container.itemName.text = data[position].name
        holder.container.setOnClickListener {
            callback?.onRecordItemClicked(data[position])
        }
    }

    internal fun setData(record: List<ExerciseEntity>) {
        this.data = record
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}