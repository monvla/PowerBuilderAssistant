package com.monvla.powerbuilderassistant.ui.exercise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.ExercisesAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.record.TrainingViewModel
import kotlinx.android.synthetic.main.screen_dairy_select_exercise.*


class SelectExerciseFragment : Screen(), ExercisesAdapter.ItemClick {

    private val viewModel: TrainingViewModel by activityViewModels()

    init {
        screenLayout = R.layout.screen_dairy_select_exercise
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExercisesAdapter(requireContext())

        adapter.setData(Utils.getDefaultExercisesList(resources))
        select_exercise_list.adapter = adapter
        select_exercise_list.layoutManager = LinearLayoutManager(requireContext())
        (select_exercise_list.adapter as ExercisesAdapter).callback = this
    }

    override fun onExerciseItemClicked(exerciseEntity: Utils.ExerciseJson) {
        if (!lifted_weight.text.isBlank()) {
//            val weight = lifted_weight.text.toString().toFloat()
//            val repeats = repeats.text.toString().toInt()
//            val record = ExerciseEntity(0, -1, weight, repeats, exerciseEntity.name)
//            viewModel.addExercise(record)
//            requireActivity().onBackPressed()
        }
    }

}