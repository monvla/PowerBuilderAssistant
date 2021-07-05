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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.TrainingDetailsAdapter
import com.monvla.powerbuilderassistant.ui.BottomNavigationFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultFragment
import com.monvla.powerbuilderassistant.vo.SetExercisesList
import kotlinx.android.synthetic.main.screen_dairy_record_details.*

class TrainingDetailsFragment : BottomNavigationFragment(), TrainingSetClickListener {

    companion object {
        const val KEY_TRAINING_ID = "trainingId"
        private const val CREATE_NEW_RECORD = -1L
    }

    private lateinit var trainingDetailsAdapter: TrainingDetailsAdapter

    private lateinit var viewModel: TrainingDetailsViewModel

    init {
        screenLayout = R.layout.screen_dairy_record_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val trainingId = checkNotNull(arguments?.getLong(TrainingSetResultFragment.KEY_TRAINING_ID))
        viewModel = TrainingDetailsViewModelFactory(requireActivity().application, trainingId)
                .create(TrainingDetailsViewModel::class.java)
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
                viewModel.addSetRequested(trainingInfo.trainingId, trainingInfo.trainingSets.size + 1)
            }
        }
        viewModel.addSetTrigger.subscribeChanges(viewLifecycleOwner) { trigger ->
            val args = Bundle().also {
                it.putLong(TrainingSetResultFragment.KEY_SET_ID, -1)
                it.putInt(TrainingSetResultFragment.KEY_SET_NUMBER, trigger.setNumber)
                it.putParcelable(TrainingSetResultFragment.KEY_SET_EXERCISES, SetExercisesList())
            }
            navigationRoot.navigate(this.javaClass, TrainingSetResultFragment::class.java, args)
        }
        viewModel.dataUpdatedTrigger.subscribeChanges(viewLifecycleOwner) {
            trainingDetailsAdapter.notifyDataSetChanged()
        }
        viewModel.recordDeletedTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(context, getString(R.string.training_deleted), Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
        setFragmentResultListener(TrainingSetResultFragment.KEY_TRAINING_SET_RESULT) { _, bundle ->
            val setId = checkNotNull(bundle.getLong(TrainingSetResultFragment.KEY_SET_ID))
            val setNumber = checkNotNull(bundle.getInt(TrainingSetResultFragment.KEY_SET_NUMBER))
            val setExercisesList = checkNotNull(
                bundle.getParcelable<SetExercisesList>(TrainingSetResultFragment.KEY_SET_EXERCISES)
            )
            viewModel.setUpdated(setId, setExercisesList, setNumber)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumed()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_content_menu, menu)
        val trainingId = checkNotNull(arguments?.getLong(TrainingSetResultFragment.KEY_TRAINING_ID))
        menu.findItem(R.id.save).isVisible = trainingId == CREATE_NEW_RECORD
        menu.findItem(R.id.delete).isVisible = trainingId != CREATE_NEW_RECORD
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
        val args = Bundle().also {
            it.putLong(TrainingSetResultFragment.KEY_SET_ID, setId)
            it.putInt(TrainingSetResultFragment.KEY_SET_NUMBER, setNumber)
            it.putParcelable(TrainingSetResultFragment.KEY_SET_EXERCISES, SetExercisesList())
        }
        navigationRoot.navigate(this.javaClass, TrainingSetResultFragment::class.java, args)
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