package com.monvla.powerbuilderassistant.ui.exercise

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.ExercisesAdapter
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_dairy_select_exercise.*


class SelectExerciseFragment : Screen(), ExercisesAdapter.ItemClick {

    init {
        screenLayout = R.layout.screen_dairy_select_exercise
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ExercisesAdapter(requireContext())

//        val data = DairyCreateRecordData.getAllExercisesList()
        select_exercise_list.adapter = adapter
        select_exercise_list.layoutManager = LinearLayoutManager(requireContext())
        (select_exercise_list.adapter as ExercisesAdapter).callback = this
//        Log.d("LUPA", model.selectedDairyExercises.value?.size.toString())
    }

    override fun onExerciseItemClicked(exerciseEntity: ExerciseEntity) {
        if (lifted_weight.text.isBlank()) return
        val weight = lifted_weight.text.toString().toFloat()
        val repeats = repeats.text.toString().toInt()
        requireActivity().onBackPressed()
    }
}