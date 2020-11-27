package com.monvla.powerbuilderassistant.ui.exerciseset

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
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

    private val viewModelTraining: TrainingSetResultViewModel by activityViewModels()
    private val args: TrainingSetResultFragmentArgs by navArgs()

    private var trainingSetResultAdapter: TrainingSetResultAdapter? = null

    init {
        screenLayout = R.layout.screen_set_result
    }

    private var exercisesList: List<ExerciseEntity>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModelTraining.exercises.observe(viewLifecycleOwner) {
            exercisesList = it
        }
        viewModelTraining.setExercises.observe(viewLifecycleOwner) {
            setupRecyclerView(it)
            trainingSetResultAdapter?.notifyDataSetChanged()
        }
        viewModelTraining.setNumber.observe(viewLifecycleOwner) {
            setResultsHeader.text = resources.getString(R.string.set_result_header, it)
        }
        viewModelTraining.deleteTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
            trainingSetResultAdapter?.notifyDataSetChanged()
        }
        if (args.setId != -1L) {
            viewModelTraining.loadSetExercises(args.setId)
        }
        fabAddTrainingSet.setOnClickListener {
            showDialog(null)
        }
    }

    private fun applyMenu(exercise: SetExercise, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.apply {
            inflate(R.menu.exercise_menu)
            setOnMenuItemClickListener {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_exercise, exercise.name))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModelTraining.deleteSetExercise(exercise)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                true
            }
            show()
        }
    }

    override fun onSaveSetExerciseClick(setExercise: SetExercise) {
        viewModelTraining.exerciseUpdated(setExercise)
    }

    private fun setupRecyclerView(exercisesList: List<SetExercise>) {
        trainingSetResultAdapter = TrainingSetResultAdapter(exercisesList)
        trainingSetResultAdapter?.setOnExerciseClicked { exercise ->
            showDialog(exercise)
        }
        trainingSetResultAdapter?.setOnLongExerciseClick { exercise, view ->
            applyMenu(exercise, view)
        }
        trainingSetResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainingSetResultAdapter
        }
    }

    private fun showDialog(exercise: SetExercise?) {
        val dialog = TrainingSetResultDialog(exercise, this).apply {
            exercisesList = this@TrainingSetResultFragment.exercisesList
        }
        dialog.setId = args.setId
        dialog.show(requireActivity().supportFragmentManager, DIALOG_TAG)
    }

}