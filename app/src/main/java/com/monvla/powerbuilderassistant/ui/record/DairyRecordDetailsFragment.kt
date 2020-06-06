package com.monvla.powerbuilderassistant.ui.record

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.DisplayableDairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.dairy.TrainingViewModel
import kotlinx.android.synthetic.main.screen_dairy_create_record.*

class DairyRecordDetailsFragment: Screen() {

    companion object {
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var modelTraining: TrainingViewModel
    private val viewModel: DairyRecordViewModel by activityViewModels()

    val args: DairyRecordDetailsFragmentArgs by navArgs()

    private lateinit var adapter: DisplayableDairyRecordAdapter

    init {
        screenLayout = R.layout.screen_dairy_create_record
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        viewModel.getExercisesForTraining(args.trainingId).observe(viewLifecycleOwner) {
            if (args.trainingId != CREATE_NEW_RECORD) {
                adapter.setData(it)
            }
        }
        viewModel.selectedExercises.observe(viewLifecycleOwner) {
            adapter.setData(it)
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
        adapter = DisplayableDairyRecordAdapter(requireContext())
        create_dairy_record_exercises_list.adapter = adapter
        create_dairy_record_exercises_list.layoutManager = LinearLayoutManager(requireContext())

        create_dairy_record_select_exercise_button.setOnClickListener {
            val action = DairyRecordDetailsFragmentDirections.actionScreenDairyRecordDetailsToDairyRecordSelectExercise()
            findNavController().navigate(action)
        }

        buttonCreateExercise.setOnClickListener {
            viewModel.createRecord()
            requireActivity().onBackPressed()
        }
    }

}