package com.monvla.powerbuilderassistant.ui.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.github.tmurakami.aackt.lifecycle.subscribeChanges
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.SimpleFragment
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.screen_exercise_edit.*

class ExerciseEditFragment : SimpleFragment() {

    init {
        screenLayout = R.layout.screen_exercise_edit
    }

    private val viewModel: ExerciseEditViewModel by activityViewModels()

    val args: ExerciseEditFragmentArgs by navArgs()
    var exercise: ExerciseEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.exerciseId != -1L) {
            viewModel.loadExercise(args.exerciseId)
        } else {
            viewModel.clearExercise()
        }
        viewModel.exercise.observe(viewLifecycleOwner) {
            exercise = it
            if (it == null) {
                clearFields()
            } else {
                exerciseNameField.editText?.setText(it.name)
                defaultWeightField.editText?.setText(it.defaultWeight.toString())
            }
        }
        viewModel.changed.subscribeChanges(viewLifecycleOwner) {
            requireActivity().onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_content_menu, menu)
        if (args.exerciseId == -1L) {
            menu.findItem(R.id.delete).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if (exerciseNameField.editText?.text.isNullOrBlank()) {
                    Toast.makeText(context, getString(R.string.exercise_edit_empty_exercise_name), Toast.LENGTH_SHORT).show()
                    return true
                }
                if (exercise == null) {
                    addExercise()
                } else {
                    showEditDialog()
                }
                true
            }
            R.id.delete -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearFields() {
        exerciseNameField.editText?.text = null
        defaultWeightField.editText?.setText("0.0")
    }

    private fun addExercise() {
        viewModel.addExercise(
            ExerciseEntity(
                name = exerciseNameField.editText?.text.toString(),
                defaultWeight = getWeight()
            )
        )
    }

    private fun getWeight() = if (defaultWeightField.editText?.text.isNullOrBlank()) {
        0f
    } else {
        defaultWeightField.editText?.text.toString().toFloat()
    }

    private fun changeExercise() {
        val exerciseEntity = ExerciseEntity(
            args.exerciseId,
            exerciseNameField.editText?.text.toString(),
            getWeight()
        )
        viewModel.updateExercise(exerciseEntity)
        Toast.makeText(
            context,
            getString(R.string.edit_exercise_changes_saved, exerciseEntity.name),
            Toast.LENGTH_SHORT).show()
    }

    private fun deleteExercise() {
        exercise?.let {
            viewModel.deleteExercise(it)
            Toast.makeText(context, getString(R.string.exercise_edit_deleted_exercise, it.name), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog() = context?.let {
        AlertDialog.Builder(it).apply {
            setTitle(getString(R.string.exercise_edit_delete_title))
            setMessage(getString(R.string.exercise_edit_delete_body))
            setPositiveButton(android.R.string.ok) { _, _ ->
                deleteExercise()
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }

    private fun showEditDialog() = context?.let {
        AlertDialog.Builder(it).apply {
            setTitle(getString(R.string.exercise_edit_change_title))
            setMessage(getString(R.string.exercise_edit_change_body))
            setPositiveButton(android.R.string.ok) { _, _ ->
                changeExercise()
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }
}