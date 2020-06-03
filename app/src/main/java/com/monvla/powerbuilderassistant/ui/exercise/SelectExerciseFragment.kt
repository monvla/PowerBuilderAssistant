package com.monvla.powerbuilderassistant.ui.exercise

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.ExercisesAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import com.monvla.powerbuilderassistant.vo.TrainingRecord
import kotlinx.android.synthetic.main.screen_dairy_select_exercise.*


class SelectExerciseFragment : Screen(), ExercisesAdapter.ItemClick {

    private val viewModel: DairyRecordViewModel by activityViewModels()

    init {
        screenLayout = R.layout.screen_dairy_select_exercise
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExercisesAdapter(requireContext())

//        val data = DairyCreateRecordData.getAllExercisesList()
        adapter.setData(viewModel.getExercisesList(resources))
        select_exercise_list.adapter = adapter
        select_exercise_list.layoutManager = LinearLayoutManager(requireContext())
        (select_exercise_list.adapter as ExercisesAdapter).callback = this
//        Log.d("LUPA", model.selectedDairyExercises.value?.size.toString())
    }

    override fun onExerciseItemClicked(exerciseEntity: DairyRecordViewModel.Exercise) {
        if (!lifted_weight.text.isBlank()) {
            val weight = lifted_weight.text.toString().toFloat()
            val repeats = repeats.text.toString().toInt()
            val record = DairyRecordViewModel.Exercise(0, exerciseEntity.name, weight, repeats)
            viewModel.addExercise(record)
            requireActivity().onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_exercise_unit -> {
            if (!lifted_weight.text.isBlank()) {
                val weight = lifted_weight.text.toString().toFloat()
                val repeats = repeats.text.toString().toInt()
                requireActivity().onBackPressed()
                true
            } else {
                false
            }
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}