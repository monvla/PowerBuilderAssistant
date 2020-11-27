package com.monvla.powerbuilderassistant.ui.record

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.TrainingDetailsAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import com.monvla.powerbuilderassistant.ui.realtimetraining.SetResultDialogFragment
import kotlinx.android.synthetic.main.screen_dairy_record_details.*


class TrainingDetailsFragment: Screen(), TrainingSetClickListener {

    companion object {
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var trainingDetailsAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel: TrainingViewModel by activityViewModels()

    val args: TrainingDetailsFragmentArgs by navArgs()

    init {
        screenLayout = R.layout.screen_dairy_record_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        viewModel.training.observe(viewLifecycleOwner) {
            setupTrainingView(it)
        }
        viewModel.recordDeleted.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(context, "Тренировка удалена", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
        viewModel.getTrainingData(args.trainingId)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun setupTrainingView(training: TrainingViewModel.Training) {
        trainingLength.text = "Длительность тренировки: ${Utils.getFormattedTimeFromSeconds(training.length)}"
        trainingSetAverageLength.text = "Среднее время на подход: ${Utils.getFormattedTimeFromSeconds(training.getAverageSetLength())}"
        trainingTotalWeight.text = "Общий поднятый вес: ${if (training.getTotalWeight() > 0) training.getTotalWeight() else "нет"}"
        trainingDetailsAdapter = TrainingDetailsAdapter(training.trainingSets, this)
        recyclerTrainingInfo.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = trainingDetailsAdapter
        }
    }

    fun setupViews() {
        setUpButtonEnabled(true)
        viewManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_content_menu, menu)
        menu.findItem(R.id.save).isVisible = args.trainingId == CREATE_NEW_RECORD
        menu.findItem(R.id.delete).isVisible = args.trainingId != CREATE_NEW_RECORD
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save -> {
                requireActivity().onBackPressed()
            }
            R.id.delete -> {
                showDeleteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteDialog() = context?.let { AlertDialog.Builder(it).apply {
            setTitle("Удалить тренировку?")
            setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteRecord(args.trainingId)
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }

    override fun onSetClick(setNumber: Int, setId: Long) {
        val action = TrainingDetailsFragmentDirections.actionScreenDairyRecordDetailsToExerciseSetResultFragment(setId = setId)
        this.findNavController().navigate(action)
    }

//    private fun showSetExercisesDialog() {
//        activity?.let {
//            val fragment = SetResultDialogFragment(viewModel.getLoadedExercises())
//            fragment.listener = this
//            fragment.show(it.supportFragmentManager, fragment.javaClass.simpleName)
//        }
//    }

//    override fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<SetResultDialogFragment.SetExercise>) {
//        Log.d("LUPA", "dialog positive")
//    }
}