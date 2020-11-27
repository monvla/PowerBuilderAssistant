package com.monvla.powerbuilderassistant.ui.exerciseset

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
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

class TrainingSetResultDialog(var exercise: SetExercise?, private val callback: TrainingSetDialogListener) : DialogFragment() {

    companion object {
        const val DIALOG_TAG = "training_set_result_dialog"
    }

    interface TrainingSetDialogListener {
        fun onSaveSetExerciseClick(setExercise: SetExercise)
    }

    var setId: Long? = null
    var exercisesList: List<ExerciseEntity>? = null
    var spinnerAdapter: ArrayAdapter<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater;
        val layout = inflater.inflate(R.layout.training_set_result_dialog, null) as ConstraintLayout

        exercisesList?.let { exercise ->
            val values = exercise.map { it.name }
            spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, values)
            spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        layout.exercisesSpinner.apply {
            adapter = spinnerAdapter
        }

        exercise?.let {
            layout.exercisesSpinner.setSelectionByName(it.name)
            layout.exerciseRepeatsText.setText(it.repeats.toString())
            layout.exerciseWeightText.setText(it.weight.toString())
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
                    val name = layout.exercisesSpinner.selectedItem.toString()
                    val repeats = layout.exerciseRepeatsText.text.toString().toInt()
                    val weight = with(layout.exerciseWeightText.text) {
                        if (isNullOrBlank()) {
                            0f
                        } else {
                            toString().toFloat()
                        }
                    }
                    updateExerciseData(name, repeats, weight)
                    callback.onSaveSetExerciseClick(requireNotNull(exercise))
                    dialogInterface.dismiss()
                }
            }
        }
    }

    private fun updateExerciseData(name: String, repeats: Int, weight: Float) {
        if (exercise == null) {
            exercise = SetExercise(
                setId = requireNotNull(setId),
                name = name,
                repeats = repeats,
                weight = weight
            )
        } else {
            requireNotNull(exercise).apply {
                this.name = name
                this.repeats = repeats
                this.weight = weight
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