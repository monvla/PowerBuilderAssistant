package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.vo.SetExercise
import kotlinx.android.synthetic.main.item_set_result_exercise.view.*


class TrainingSetResultAdapter(private val exercisesList: List<SetExercise>) :
    RecyclerView.Adapter<TrainingSetResultAdapter.TrainingSetResultViewHolder>() {

    var callback: (SetExercise) -> Unit = {}
    var longClickCallback: (SetExercise, View) -> Unit = { setExercise: SetExercise, view: View -> }

    class TrainingSetResultViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainingSetResultViewHolder {
        val viewGroup = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_set_result_exercise, parent, false) as ViewGroup
        return TrainingSetResultViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: TrainingSetResultViewHolder, position: Int) {
        val exercise = exercisesList[position]
        holder.viewGroup.setExerciseName.text = exercise.exerciseName
        holder.viewGroup.setExerciseRepeats.text = exercise.repeats.toString()
        if (exercise.weight != 0f) {
            holder.viewGroup.setExerciseWeight.text = exercise.weight.toString()
        } else {
            holder.viewGroup.setExerciseWeight.text = "нет"
            holder.viewGroup.setExerciseWeightUnitLabel.isVisible = false
        }
        holder.viewGroup.setOnClickListener {
            callback(exercise)
        }
        holder.viewGroup.setOnLongClickListener {
            longClickCallback(exercise, it)
            true
        }
    }

    override fun getItemCount() = exercisesList.size

    fun onExerciseClicked(callback: (SetExercise) -> Unit) {
        this.callback = callback
    }

    fun onLongExerciseClick(callback: (SetExercise, View) -> Unit) {
        this.longClickCallback = callback
    }
}
