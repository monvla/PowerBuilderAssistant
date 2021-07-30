package com.monvla.powerbuilderassistant.ui.exerciseset

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribe
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.TrainingSetResultAdapter
import com.monvla.powerbuilderassistant.ui.SimpleFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.SetExerciseDialog.Companion.DIALOG_TAG
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.SetExercisesList
import com.monvla.powerbuilderassistant.vo.UNDEFINED_ID
import kotlinx.android.synthetic.main.screen_set_result.*

class TrainingSetResultFragment : SimpleFragment(), SetExerciseDialog.TrainingSetDialogListener {

    companion object {
        const val KEY_TRAINING_SET_RESULT = "trainingSetResult"
        const val KEY_TRAINING_ID = "trainingId"
        const val KEY_SET_ID = "setId"
        const val KEY_SET_NUMBER = "setNumber"
        const val KEY_SET_EXERCISES = "setExercises"
    }

    private lateinit var viewModelFactory: TrainingSetResultViewModelFactory
    private lateinit var viewModel: TrainingSetResultViewModel

    private var setId: Long? = null
    private var setNumber: Int? = null
    private var setExercises: SetExercisesList? = null

    init {
        screenLayout = R.layout.screen_set_result
    }

    private var dialog: SetExerciseDialog? = null

    private lateinit var trainingSetResultAdapter: TrainingSetResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(requireArguments()) {
            setId = getLong(KEY_SET_ID)
            setNumber = getInt(KEY_SET_NUMBER)
            setExercises = checkNotNull(getParcelable(KEY_SET_EXERCISES))
        }
        viewModelFactory = TrainingSetResultViewModelFactory(
            requireContext(),
            checkNotNull(setId),
            checkNotNull(setNumber),
            checkNotNull(setExercises)
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainingSetResultViewModel::class.java)
        navigationRoot.setBottomNavigationVisible(false)
        setHasOptionsMenu(true)
        navigationRoot.setHomeAsUpEnabled(!navigationRoot.isTrainingInProgress())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setData.subscribe(viewLifecycleOwner) {
            setResultsHeader.text = resources.getString(R.string.set_result_header, it.setNumber)
            setupRecyclerView(it.setExercises)
            trainingSetResultAdapter.notifyDataSetChanged()
        }
        viewModel.showAddExerciseDialog.subscribeChanges(viewLifecycleOwner) {
            val exercise = it.setExercise ?: SetExercise.createEmpty()
            if (setId != UNDEFINED_ID) {
                exercise.setId = checkNotNull(setId)
            }
            showDialog(exercise, it.exercisesList)
        }
        viewModel.deleteTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
            trainingSetResultAdapter.notifyDataSetChanged()
        }
        fabAddTrainingSet.setOnClickListener {
            viewModel.prepareNewExerciseDialog(null)
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onStop() {
        super.onStop()
        navigationRoot.setHomeAsUpEnabled(navigationRoot.isTrainingInProgress())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_set_exercises_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.apply -> {
                setFragmentResult(
                    KEY_TRAINING_SET_RESULT, bundleOf(
                        KEY_SET_ID to setId,
                        KEY_SET_NUMBER to setNumber,
                        KEY_SET_EXERCISES to viewModel.getSetExercises()
                    )
                )
                navigationRoot.finishFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveSetExerciseClick(setExercise: SetExercise) {
        viewModel.exerciseUpdated(setExercise)
    }

    private fun applyMenu(exercise: SetExercise, view: View) {
        PopupMenu(requireContext(), view).also {
            it.inflate(R.menu.changeable_item_menu)
            it.setOnMenuItemClickListener {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_exercise, exercise.exerciseName))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.deleteSetExerciseRequested(exercise)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show()
                true
            }
            it.show()
        }
    }

    private fun setupRecyclerView(exercisesList: List<SetExercise>) {
        trainingSetResultAdapter = TrainingSetResultAdapter(exercisesList).apply {
            onExerciseClicked { exercise ->
                viewModel.prepareNewExerciseDialog(exercise)
            }
            onLongExerciseClick { exercise, view ->
                applyMenu(exercise, view)
            }
        }
        trainingSetResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainingSetResultAdapter
        }
    }

    private fun showDialog(exercise: SetExercise, exercisesList: List<ExerciseEntity>) {
        dialog = SetExerciseDialog(exercise, exercisesList, this).also {
            it.show(requireActivity().supportFragmentManager, DIALOG_TAG)
        }
    }
}