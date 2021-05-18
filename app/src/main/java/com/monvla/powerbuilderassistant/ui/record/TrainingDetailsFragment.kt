package com.monvla.powerbuilderassistant.ui.record

import android.app.Application
import android.os.Bundle
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
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.TrainingDetailsAdapter
import com.monvla.powerbuilderassistant.ui.BottomNavigationFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultViewModel
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultViewModel.Companion.FRAGMENT_RESULT_KEY
import com.monvla.powerbuilderassistant.vo.SetExercisesList
import kotlinx.android.synthetic.main.screen_dairy_record_details.*

class TrainingDetailsFragment : BottomNavigationFragment(), TrainingSetClickListener {

    companion object {
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var trainingDetailsAdapter: TrainingDetailsAdapter

    private val viewModel: TrainingDetailsViewModel by viewModels {
        TrainingDetailsViewModelFactory(requireActivity().application, args.trainingId)
    }

    private val args: TrainingDetailsFragmentArgs by navArgs()

    init {
        screenLayout = R.layout.screen_dairy_record_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        trainingDetailsAdapter = TrainingDetailsAdapter(resources, emptyList(), this)
        setupViews()

        viewModel.trainingInfo.subscribeChanges(viewLifecycleOwner) { trainingInfo ->
            trainingDetailsAdapter.setTrainingSets(trainingInfo.trainingSets)
            trainingLength.text = getString(
                R.string.training_length, Utils.getFormattedTimeFromSeconds(trainingInfo.length)
            )
            trainingSetAverageLength.text = getString(
                R.string.training_average_set_time,
                Utils.getFormattedTimeFromSeconds(trainingInfo.getAverageSetLength())
            )
            trainingTotalWeight.text = getString(
                R.string.training_total_weight,
                if (trainingInfo.getTotalWeight() > 0) trainingInfo.getTotalWeight() else getString(R.string.set_empty_weight)
            )
            addRecordFab.setOnClickListener {
                viewModel.addSetRequested(args.trainingId, trainingInfo.trainingSets.size + 1)
            }
        }
        viewModel.addSetTrigger.subscribeChanges(viewLifecycleOwner) {
            val action = TrainingDetailsFragmentDirections.actionTrainingDetailsFragmentToExerciseSetResultFragment(
                setId = it.setId,
                setNumber = it.setNumber,
                setExercises = SetExercisesList()
            )
            this.findNavController().navigate(action)
        }
        viewModel.dataUpdatedTrigger.subscribeChanges(viewLifecycleOwner) {
            trainingDetailsAdapter.notifyDataSetChanged()
        }
        viewModel.recordDeletedTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(context, getString(R.string.training_deleted), Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.let {
            it.getLiveData<TrainingSetResultViewModel.FragmentResult>(FRAGMENT_RESULT_KEY).observe(
                viewLifecycleOwner
            ) { result ->
                viewModel.setUpdated(result.setId, result.setExercisesList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumed()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_content_menu, menu)
        menu.findItem(R.id.save).isVisible = args.trainingId == CREATE_NEW_RECORD
        menu.findItem(R.id.delete).isVisible = args.trainingId != CREATE_NEW_RECORD
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                requireActivity().onBackPressed()
            }
            R.id.delete -> {
                showDeleteDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSetClick(setNumber: Int, setId: Long) {
        val action = TrainingDetailsFragmentDirections.actionTrainingDetailsFragmentToExerciseSetResultFragment(
            setId = setId,
            setNumber = setNumber,
            setExercises = SetExercisesList()
        )
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

    private fun setupViews() {
        setUpButtonEnabled(true)

        recyclerTrainingInfo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainingDetailsAdapter
        }
    }

    private fun showDeleteDialog() = AlertDialog.Builder(requireContext()).apply {
        setTitle(getString(R.string.delete_training_question))
        setPositiveButton(R.string.delete) { _, _ ->
            viewModel.deleteRecord()
        }
        setNegativeButton(android.R.string.cancel, null)
    }.show()

    class TrainingDetailsViewModelFactory(val application: Application, private val trainingId: Long) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TrainingDetailsViewModel(application, trainingId) as T
        }
    }
}