package com.monvla.powerbuilderassistant.ui.exerciseset

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.TrainingSetResultAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.exerciseset.TrainingSetResultDialog.Companion.DIALOG_TAG
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import kotlinx.android.synthetic.main.screen_set_result.*

class TrainingSetResultFragment : Screen(), TrainingSetResultDialog.TrainingSetDialogListener {

    private val args: TrainingSetResultFragmentArgs by navArgs()

    private val viewModel: TrainingSetResultViewModel by viewModels {
        TrainingSetResultViewModelFactory(requireActivity().application, args.setId)
    }

    init {
        screenLayout = R.layout.screen_set_result
    }

    private lateinit var trainingSetResultAdapter: TrainingSetResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (args.newSetRequired) {
            setUpButtonEnabled(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setData.observe(viewLifecycleOwner) {
            setResultsHeader.text = resources.getString(R.string.set_result_header, it.setNumber)
            setupRecyclerView(it.setExercises)
            trainingSetResultAdapter.notifyDataSetChanged()
        }
        viewModel.showAddExerciseDialog.subscribeChanges(viewLifecycleOwner) {
            showDialog(it.currentExercise, it.exercisesList)
        }
        viewModel.deleteTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
            trainingSetResultAdapter.notifyDataSetChanged()
        }
        fabAddTrainingSet.setOnClickListener {
            viewModel.prepareNewExerciseDialog(null)
        }
        if (args.newSetRequired) {
            viewModel.prepareNewExerciseDialog(null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_set_exercises_menu, menu)
        if (args.newSetRequired) {
            menu.findItem(R.id.apply).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.apply -> requireActivity().onBackPressed()
        }
        return true
    }

    override fun onSaveSetExerciseClick(setExercise: SetExercise) {
        viewModel.exerciseUpdated(setExercise)
    }

    private fun applyMenu(exercise: SetExercise, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.apply {
            inflate(R.menu.changeable_item_menu)
            setOnMenuItemClickListener {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_exercise, exercise.name))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.deleteSetExerciseRequested(exercise)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                true
            }
            show()
        }
    }

    private fun setupRecyclerView(exercisesList: List<SetExercise>) {
        trainingSetResultAdapter = TrainingSetResultAdapter(exercisesList).apply {
            setOnExerciseClicked { exercise ->
                viewModel.prepareNewExerciseDialog(exercise)
            }
            setOnLongExerciseClick { exercise, view ->
                applyMenu(exercise, view)
            }
        }
        trainingSetResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainingSetResultAdapter
        }
    }

    private fun showDialog(exercise: SetExercise?, exercisesList: List<ExerciseEntity>) =
            TrainingSetResultDialog(exercise, exercisesList, this).apply {
                setId = args.setId
            }.show(requireActivity().supportFragmentManager, DIALOG_TAG)
}