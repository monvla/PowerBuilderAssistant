package com.monvla.powerbuilderassistant.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.BuildConfig
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.BottomNavigationFragment
import com.monvla.powerbuilderassistant.ui.SimpleFragment
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragment.Companion.DESTINATION_EXERCISE_EDIT
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.item_exercise_list.view.*
import kotlinx.android.synthetic.main.screen_exercises_list.*

class ExercisesListFragment : BottomNavigationFragment(), ExerciseClickListener {

    init {
        screenLayout = R.layout.screen_exercises_list
    }

    val args: ExercisesListFragmentArgs by navArgs()

    private val viewModel: ExercisesListViewModel by activityViewModels()
    private lateinit var exercisesAdapter: ExercisesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.destination == DESTINATION_EXERCISE_EDIT) {
            button_add_exercise.apply {
                setOnClickListener {
                    val action = ExercisesListFragmentDirections.actionExercisesListFragmentToExerciseEditFragment(-1)
                    it.findNavController().navigate(action)
                }
                isVisible = true
            }
        }
        version.text = "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR}"
        exercisesAdapter = ExercisesListAdapter(listOf(), this)
        recycler_exercises_list.apply {
            setHasFixedSize(true)

            layoutManager = LinearLayoutManager(context)

            adapter = exercisesAdapter
        }
        viewModel.exercises.observe(viewLifecycleOwner) {
            exercisesAdapter.setData(it)
            exercisesAdapter.notifyDataSetChanged()
        }
    }

    class ExercisesListAdapter(private var dataset: List<ExerciseEntity>, var callback: ExerciseClickListener) :
        RecyclerView.Adapter<ExercisesListAdapter.ExercisesListViewHolder>() {

        class ExercisesListViewHolder(val group: ViewGroup) : RecyclerView.ViewHolder(group)


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ExercisesListViewHolder {
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
                callback.onExerciseClicked(it, dataset[position].id)
            }
        }

        override fun getItemCount() = dataset.size

        fun setData(data: List<ExerciseEntity>) {
            dataset = data
        }
    }

    override fun onExerciseClicked(view: View, exerciseId: Long) {
        when (args.destination) {
            DESTINATION_EXERCISE_EDIT -> {
                val action =
                        ExercisesListFragmentDirections.actionExercisesListFragmentToExerciseEditFragment(exerciseId)
                view.findNavController().navigate(action)
            }
//            DESTINATION_EXERCISE_STATISTICS -> {
//                val action = ExercisesListFragmentDirections.actionExercisesListFragmentToExerciseStatisticsFragment(exerciseId)
//                view.findNavController().navigate(action)
//            }
        }
    }
}

interface ExerciseClickListener {
    fun onExerciseClicked(view: View, exerciseId: Long)
}