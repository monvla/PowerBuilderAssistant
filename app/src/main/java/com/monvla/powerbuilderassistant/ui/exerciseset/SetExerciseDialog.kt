package com.monvla.powerbuilderassistant.ui.exerciseset

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import com.monvla.powerbuilderassistant.vo.SetExercise
import kotlinx.android.synthetic.main.training_set_result_dialog.view.*

class SetExerciseDialog(
    var exercise: SetExercise,
    var exercisesList: List<ExerciseEntity>,
    private val callback: TrainingSetDialogListener
) : DialogFragment() {

    companion object {
        const val DIALOG_TAG = "training_set_result_dialog"
    }

    interface TrainingSetDialogListener {
        fun onSaveSetExerciseClick(setExercise: SetExercise)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater;
        val layout = inflater.inflate(R.layout.training_set_result_dialog, null) as ConstraintLayout

        val namesList = exercisesList.map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namesList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        layout.apply {
            exercisesSpinner.apply {
                adapter = spinnerAdapter
                setSelectionByName(exercise.exerciseName)
            }
            val repeatsString = if (exercise.repeats == 0) "" else exercise.repeats.toString()
            exerciseRepeatsText.setText(repeatsString)
            val weightString = if (exercise.weight == 0f) "" else exercise.weight.toString()
            exerciseWeightText.setText(weightString)
        }

        builder.setView(layout)
                .setTitle(R.string.done_exercise_title)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
        return builder.create().apply {
            setOnShowListener { dialogInterface ->
                getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (layout.exerciseRepeatsText.text.isNullOrBlank()) {
                        Toast.makeText(requireContext(), getString(R.string.save_set_dialog_error), Toast.LENGTH_LONG)
                                .show()
                        return@setOnClickListener
                    }

                    val nameValue = layout.exercisesSpinner.selectedItem.toString()
                    val repeatsValue = layout.exerciseRepeatsText.text.toString().toInt()
                    val weightText = layout.exerciseWeightText.text.toString()
                    val weightValue = if (weightText.isBlank()) 0f else weightText.toFloat()
                    exercise.update(nameValue, repeatsValue, weightValue)

                    callback.onSaveSetExerciseClick(exercise)
                    dialogInterface.dismiss()
                }
            }
        }
    }

    private fun Spinner.setSelectionByName(name: String) {
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == name) {
                setSelection(i)
            }
        }
    }
}