package com.monvla.powerbuilderassistant.ui.record

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.DisplayableDairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.dairy.TrainingViewModel
import kotlinx.android.synthetic.main.screen_dairy_create_record.*
import kotlinx.coroutines.launch

class CreateDairyRecordFragment: Screen() {

    private lateinit var modelTraining: TrainingViewModel
    private val viewModel: DairyRecordViewModel by activityViewModels()

    private lateinit var adapter: DisplayableDairyRecordAdapter

    init {
        screenLayout = R.layout.screen_dairy_create_record
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        lifecycleScope.launch {
            val trainings = modelTraining.getTrainingWithExercises()
            val i = 0
        }
        viewModel.selectedExercises.observe(viewLifecycleOwner) {
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("BLA", "BLA")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        DairyCreateRecordData.clear()
    }

    fun setupViews() {
        setUpButtonEnabled(true)
        modelTraining = ViewModelProvider(requireActivity()).get(TrainingViewModel::class.java)
        modelTraining.selectedDairyExercises.observe(viewLifecycleOwner) {
            lifecycleScope.launch {

            }
        }
        adapter = DisplayableDairyRecordAdapter(requireContext())
        create_dairy_record_exercises_list.adapter = adapter
        create_dairy_record_exercises_list.layoutManager = LinearLayoutManager(requireContext())

        create_dairy_record_select_exercise_button.setOnClickListener {
            val action =
                    CreateDairyRecordFragmentDirections.actionScreenDairyCreateRecordToDairyRecordSelectExercise()
            findNavController().navigate(action)
        }

        buttonCreateExercise.setOnClickListener {
//            model.getValues()?.let {
//                val record = DairyRecord(calendarView.date, it)
//                modelDairy.select(record)
//            }
            requireActivity().onBackPressed()
        }
    }

}