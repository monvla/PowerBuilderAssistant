package com.monvla.powerbuilderassistant.ui.record

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.android.synthetic.main.screen_dairy_record_details.*


class TrainingDetailsFragment: Screen(), TrainingSetClickListener {

    companion object {
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var trainingDetailsAdapter: TrainingDetailsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel: TrainingDetailsViewModel by viewModels {
        TrainingDetailsViewModelFactory(requireActivity().application, args.trainingId)
    }

    val args: TrainingDetailsFragmentArgs by navArgs()

    init {
        screenLayout = R.layout.screen_dairy_record_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()

        trainingDetailsAdapter = TrainingDetailsAdapter(emptyList(), this)
        recyclerTrainingInfo.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = trainingDetailsAdapter
        }

        viewModel.trainingInfo.subscribeChanges(viewLifecycleOwner) { trainingInfo ->
            trainingDetailsAdapter.setTrainingSets(trainingInfo.trainingSets)
            trainingLength.text = "Длительность тренировки: ${Utils.getFormattedTimeFromSeconds(trainingInfo.length)}"
            trainingSetAverageLength.text = "Среднее время на подход: ${Utils.getFormattedTimeFromSeconds(trainingInfo.getAverageSetLength())}"
            trainingTotalWeight.text = "Общий поднятый вес: ${if (trainingInfo.getTotalWeight() > 0) trainingInfo.getTotalWeight() else "нет"}"
            addRecordFab.setOnClickListener {
                viewModel.addSetRequested(args.trainingId, trainingInfo.trainingSets.size + 1)
            }
        }
        viewModel.addSetTrigger.subscribeChanges(viewLifecycleOwner) {
            val action = TrainingDetailsFragmentDirections.actionScreenDairyRecordDetailsToExerciseSetResultFragment(
                setId = it.newSetId,
                trainingId = args.trainingId
            )
            this.findNavController().navigate(action)
        }
        viewModel.dataUpdatedTrigger.subscribeChanges(viewLifecycleOwner) {
            trainingDetailsAdapter.notifyDataSetChanged()
        }
        viewModel.recordDeletedTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(context, "Тренировка удалена", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumed()
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
                viewModel.deleteRecord()
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }

    override fun onSetClick(setNumber: Int, setId: Long) {
        val action = TrainingDetailsFragmentDirections.actionScreenDairyRecordDetailsToExerciseSetResultFragment(setId = setId)
        this.findNavController().navigate(action)
    }

    override fun onLongSetClick(setId: Long, setViewGroup: ViewGroup) {
        applyMenu(setId, setViewGroup)
    }

    private fun applyMenu(setNumber: Long, setViewGroup: ViewGroup) {
        val menu = PopupMenu(requireContext(), setViewGroup)
        menu.apply {
            inflate(R.menu.changeable_item_menu)
            setOnMenuItemClickListener {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_set))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.deleteSet(setNumber)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                true
            }
            show()
        }
    }

    class TrainingDetailsViewModelFactory(val application: Application, val trainingId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TrainingDetailsViewModel(application, trainingId) as T
        }
    }

}