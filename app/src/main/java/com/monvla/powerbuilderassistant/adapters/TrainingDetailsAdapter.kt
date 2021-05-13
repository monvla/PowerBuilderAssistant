package com.monvla.powerbuilderassistant.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.record.TrainingSetClickListener
import com.monvla.powerbuilderassistant.vo.Exercise
import com.monvla.powerbuilderassistant.vo.TrainingSet
import kotlinx.android.synthetic.main.item_exercise.view.*
import kotlinx.android.synthetic.main.item_set_exercises.view.*

class TrainingDetailsAdapter (
    val resources: Resources,
    var setsList: List<TrainingSet>,
    private val callback: TrainingSetClickListener
    ) : RecyclerView.Adapter<TrainingDetailsAdapter.TrainingDetailsViewHolder>(), TrainingSetClickListener {

    class TrainingDetailsViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

    fun setTrainingSets(newSetsList: List<TrainingSet>) {
        setsList = newSetsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainingDetailsViewHolder {
        val viewGroup = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_set_exercises, parent, false) as ViewGroup
        return TrainingDetailsViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: TrainingDetailsViewHolder, position: Int) {
        val set = setsList[position]
        holder.viewGroup.setName.text = resources.getString(R.string.set_number, set.number)

        val exercisesAdapter = SetExerciseAdapter(set.exercises, set.number, set.id, this)
        holder.viewGroup.recyclerSetExercises.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = exercisesAdapter
        }
        holder.viewGroup.setOnClickListener {
            onSetClick(set.number, set.id)
        }
        holder.viewGroup.setOnLongClickListener {
            callback.onLongSetClick(set.id, holder.viewGroup)
            true
        }
    }

    override fun getItemCount() = setsList.size

    override fun onSetClick(setNumber: Int, setId: Long) {
        callback.onSetClick(setNumber, setId)
    }

    override fun onLongSetClick(setId: Long, setViewGroup: ViewGroup) {
        callback.onLongSetClick(setId, setViewGroup)
    }

    inner class SetExerciseAdapter(
        private val exercisesList: List<Exercise>,
        private val setNumber: Int,
        private val setId: Long,
        private val callback: TrainingSetClickListener
    ) :
        RecyclerView.Adapter<SetExerciseAdapter.SetExerciseViewHolder>() {

        inner class SetExerciseViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SetExerciseViewHolder {
            val viewGroup = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exercise, parent, false) as ViewGroup
            return SetExerciseViewHolder(viewGroup)
        }

        override fun onBindViewHolder(holder: SetExerciseViewHolder, position: Int) {
            val exercise = exercisesList[position]
            holder.viewGroup.name.text = exercise.name
            holder.viewGroup.repeats.text = exercise.repeats.toString()
            val weightText = if (exercise.weight > 0.0) {
                exercise.weight.toString()
            } else {
                resources.getString(R.string.set_empty_weight)
            }
            holder.viewGroup.weight.text = weightText
            holder.viewGroup.setOnClickListener {
                callback.onSetClick(setNumber, setId)
            }
            holder.viewGroup.setOnLongClickListener {
                callback.onLongSetClick(setId, holder.viewGroup)
                true
            }
        }

        override fun getItemCount() = exercisesList.size
    }
}
