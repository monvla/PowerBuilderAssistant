package com.monvla.powerbuilderassistant.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.item_exercise.view.*

class ExercisesAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<ExercisesAdapter.ExercisesViewHolder>() {

    var callback : ItemClick? = null

    private var data = emptyList<Utils.ExerciseJson>()

    interface ItemClick {
        fun onExerciseItemClicked(exerciseEntity: Utils.ExerciseJson)
    }

    class ExercisesViewHolder(val container: LinearLayout) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ExercisesViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false) as LinearLayout
        return ExercisesViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ExercisesViewHolder, position: Int) {
        holder.container.setOnClickListener {
            callback?.onExerciseItemClicked(data[position])
        }
    }

    internal fun setData(record: List<Utils.ExerciseJson>) {
        this.data = record
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}