package com.monvla.powerbuilderassistant.ui.record

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.TrainingDetailsAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_dairy_create_record.*


class DairyRecordDetailsFragment: Screen() {

    companion object {
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var trainingDetailsRecyclerView: RecyclerView
    private lateinit var trainingDetailsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel: DairyRecordViewModel by activityViewModels()

    val args: DairyRecordDetailsFragmentArgs by navArgs()


    init {
        screenLayout = R.layout.screen_dairy_create_record
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        viewModel.training.observe(viewLifecycleOwner) {
            setupTrainingView(it)
        }
        viewModel.getTrainingData(args.trainingId)
    }

    override fun onResume() {
        super.onResume()
        Log.d("BLA", "BLA")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun setupTrainingView(training: DairyRecordViewModel.Training) {
        trainingDetailsAdapter = TrainingDetailsAdapter(training.trainingSets)
        recyclerTrainingInfo.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = trainingDetailsAdapter
        }
    }

    fun setupViews() {
        setUpButtonEnabled(true)
        viewManager = LinearLayoutManager(requireContext())

//        create_dairy_record_select_exercise_button.setOnClickListener {
//            val action = DairyRecordDetailsFragmentDirections.actionScreenDairyRecordDetailsToDairyRecordSelectExercise()
//            findNavController().navigate(action)
//        }


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