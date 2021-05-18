package com.monvla.powerbuilderassistant.ui.exerciseset

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.TrainingSetResultAdapter
import com.monvla.powerbuilderassistant.ui.SimpleFragment
import com.monvla.powerbuilderassistant.ui.exerciseset.SetExerciseDialog.Companion.DIALOG_TAG
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultViewModel.Companion.FRAGMENT_RESULT_KEY
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultViewModel.FragmentResult
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import com.monvla.powerbuilderassistant.vo.UNDEFINED_ID
import kotlinx.android.synthetic.main.screen_set_result.*

class TrainingSetResultFragment : SimpleFragment(), SetExerciseDialog.TrainingSetDialogListener {

    private val args: TrainingSetResultFragmentArgs by navArgs()
    private lateinit var viewModelFactory: TrainingSetResultViewModelFactory
    private lateinit var viewModel: TrainingSetResultViewModel

    init {
        screenLayout = R.layout.screen_set_result
    }

    private var dialog: SetExerciseDialog? = null

    private lateinit var trainingSetResultAdapter: TrainingSetResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModelFactory = TrainingSetResultViewModelFactory(
            requireContext(),
            args.setId,
            args.setNumber,
            args.setExercises
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(TrainingSetResultViewModel::class.java)
        navigationRoot.setBottomNavigationVisible(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setData.observe(viewLifecycleOwner) {
            setResultsHeader.text = resources.getString(R.string.set_result_header, it.setNumber)
            setupRecyclerView(it.setExercises)
            trainingSetResultAdapter.notifyDataSetChanged()
        }
        viewModel.showAddExerciseDialog.subscribeChanges(viewLifecycleOwner) {
            val exercise = it.setExercise ?: SetExercise.createEmpty()
            if (args.setId != UNDEFINED_ID) {
                exercise.setId = args.setId
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_set_exercises_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.apply -> {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    FRAGMENT_RESULT_KEY, FragmentResult(args.setId, viewModel.getSetExercises())
                )
                requireActivity().onBackPressed()
            }
        }
        return true
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