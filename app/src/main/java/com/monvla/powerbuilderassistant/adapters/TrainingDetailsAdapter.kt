package com.monvla.powerbuilderassistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import kotlinx.android.synthetic.main.item_exercise.view.*
import kotlinx.android.synthetic.main.item_set_exercises.view.*
import kotlinx.android.synthetic.main.screen_dairy_create_record.view.*

class TrainingDetailsAdapter (private val setsList: List<DairyRecordViewModel.TrainingSet2>) :
    RecyclerView.Adapter<TrainingDetailsAdapter.TrainingDetailsViewHolder>() {

    class TrainingDetailsViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TrainingDetailsAdapter.TrainingDetailsViewHolder {
        val viewGroup = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_set_exercises, parent, false) as ViewGroup
        return TrainingDetailsViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: TrainingDetailsViewHolder, position: Int) {
        val set = setsList[position]
        holder.viewGroup.setName.text = "Подход ${set.number}"

        val exercisesAdapter = SetExerciseAdapter(set.exercises)
        holder.viewGroup.recyclerSetExercises.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = exercisesAdapter
        }
    }

    override fun getItemCount() = setsList.size

    class SetExerciseAdapter(private val exercisesList: List<DairyRecordViewModel.Exercise>) :
        RecyclerView.Adapter<SetExerciseAdapter.SetExerciseViewHolder>() {

        class SetExerciseViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): SetExerciseAdapter.SetExerciseViewHolder {
            val viewGroup = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exercise, parent, false) as ViewGroup
            return SetExerciseViewHolder(viewGroup)
        }

        override fun onBindViewHolder(holder: SetExerciseViewHolder, position: Int) {
            val exercise = exercisesList[position]
            holder.viewGroup.name.text = exercise.name
            holder.viewGroup.repeats.text = exercise.repeats.toString()
            holder.viewGroup.weight.text = if (exercise.weight > 0.0) exercise.weight.toString() else "нет"
        }

        override fun getItemCount() = exercisesList.size
    }
}