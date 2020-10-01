package com.monvla.powerbuilderassistant.ui.record

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
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
        viewModel.training.observe(viewLifecycleOwner) {
            val i = 0
            Toast.makeText(context, "LUPA: $it", Toast.LENGTH_LONG).show()
        }
        viewModel.getTrainingData()
    }

    override fun onResume() {
        super.onResume()
        Log.d("BLA", "BLA")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.training_details_menu, menu)
        menu.findItem(R.id.save_training).isVisible = args.trainingId == CREATE_NEW_RECORD
        menu.findItem(R.id.delete_training).isVisible = args.trainingId != CREATE_NEW_RECORD
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save_training -> {
                requireActivity().onBackPressed()
            }
            R.id.delete_training -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

}