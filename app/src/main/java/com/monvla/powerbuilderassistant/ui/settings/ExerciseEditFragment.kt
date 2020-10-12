package com.monvla.powerbuilderassistant.ui.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.ui.dairy.TrainingDairyFragmentDirections
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.screen_exercise_edit.*

class ExerciseEditFragment : Screen() {

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
        viewModel.loadExercise(args.exerciseId)
        viewModel.exercise.observe(viewLifecycleOwner) {
            exercise = it
            exerciseNameField.editText?.setText(it.name)
            defaultWeightField.editText?.setText(it.defaultWeight.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_content_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save -> {
                showEditDialog()
                true
            }
            R.id.delete -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun saveExercise() {
        val exerciseEntity = ExerciseEntity(
            args.exerciseId,
            exerciseNameField.editText?.text.toString(),
            defaultWeightField.editText?.text.toString().toFloat()
        )
        viewModel.updateExercise(exerciseEntity)
        requireActivity().onBackPressed()
        Toast.makeText(context, "Изменения сохранены: ${exerciseEntity.name}", Toast.LENGTH_SHORT).show()
    }

    fun deleteExercise() {
        requireActivity().onBackPressed()
        Toast.makeText(context, "DELETE: ${exerciseNameField.editText?.text.toString()}", Toast.LENGTH_SHORT).show()
    }

    fun showDeleteDialog() = context?.let { AlertDialog.Builder(it).apply {
        setTitle("Удаление")
        setMessage("Удалить упражнение? Оно будет также удалено из истории тренировок.")
        setPositiveButton(android.R.string.ok) { dialog, id ->
            deleteExercise()
        }
        setNegativeButton(android.R.string.cancel, null)
    }.show()
    }

    fun showEditDialog() = context?.let { AlertDialog.Builder(it).apply {
            setTitle("Изменение")
            setMessage("Сохранить изменения?")
            setPositiveButton(android.R.string.ok) { dialog, id ->
                saveExercise()
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }
}