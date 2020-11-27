package com.monvla.powerbuilderassistant.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.screen_exercises_list.*
import kotlinx.android.synthetic.main.item_exercise_list.view.*

class ExercisesListFragment : Screen() {

    init {
        screenLayout = R.layout.screen_exercises_list
    }

    private val viewModel: ExercisesListViewModel by activityViewModels()
    private lateinit var exercisesAdapter: ExercisesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        exercisesAdapter = ExercisesListAdapter(listOf())
        recycler_exercises_list.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(context)

            adapter = exercisesAdapter
        }
        viewModel.exercises.observe(viewLifecycleOwner) {
            exercisesAdapter.setData(it)
            exercisesAdapter.notifyDataSetChanged()
        }
        button_add_exercise.setOnClickListener {
            val action = ExercisesListFragmentDirections.actionExercisesListFragmentToExerciseEditFragment(-1)
            it.findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    class ExercisesListAdapter(private var dataset: List<ExerciseEntity>) :
        RecyclerView.Adapter<ExercisesListAdapter.ExercisesListViewHolder>() {

        class ExercisesListViewHolder(val group: ViewGroup) : RecyclerView.ViewHolder(group)


        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ExercisesListViewHolder {
            val group = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exercise_list, parent, false) as ViewGroup
            return ExercisesListViewHolder(group)
        }

        override fun onBindViewHolder(holder: ExercisesListViewHolder, position: Int) {
            val exerciseName = holder.group.exerciseName
            exerciseName.apply {
                text = dataset[position].name

            }
            holder.group.setOnClickListener {
                val action = ExercisesListFragmentDirections.actionExercisesListFragmentToExerciseEditFragment(dataset[position].id)
                it.findNavController().navigate(action)
            }
        }

        override fun getItemCount() = dataset.size

        fun setData(data: List<ExerciseEntity>) {
            dataset = data
        }
    }

}