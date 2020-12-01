package com.monvla.powerbuilderassistant.ui.exerciseset

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    private val viewModel: TrainingSetResultViewModel by activityViewModels()
    private val args: TrainingSetResultFragmentArgs by navArgs()

    private var trainingSetResultAdapter: TrainingSetResultAdapter? = null

    init {
        screenLayout = R.layout.screen_set_result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (args.isNewSet) {
            Log.d("LUPA", "AB: ${(requireActivity() as AppCompatActivity).supportActionBar}")
            setUpButtonEnabled(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setExercises.observe(viewLifecycleOwner) {
            setupRecyclerView(it)
            trainingSetResultAdapter?.notifyDataSetChanged()
        }
        viewModel.mediatorLiveData.subscribeChanges(viewLifecycleOwner) {
            Log.d("LUPA", "MEDIATOR LUPA!!! ${it.currentExercise}")
            showDialog(it.currentExercise, it.exercisesList)
        }
        viewModel.setNumber.observe(viewLifecycleOwner) {
            setResultsHeader.text = resources.getString(R.string.set_result_header, it)
        }
        viewModel.deleteTrigger.subscribeChanges(viewLifecycleOwner) {
            Toast.makeText(requireContext(), getString(R.string.exercise_deleted), Toast.LENGTH_SHORT).show()
            trainingSetResultAdapter?.notifyDataSetChanged()
        }
        viewModel.loadSetExercises(args.setId, args.isNewSet)
        fabAddTrainingSet.setOnClickListener {
            viewModel.prepareNewExerciseDialog(null)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_set_exercises_menu, menu)
        Log.d("LUPA", "IsNewSet: ${args.isNewSet}")
        if (args.isNewSet) {
            menu.findItem(R.id.apply).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.apply -> requireActivity().onBackPressed()
        }
        return true
    }

    private fun applyMenu(exercise: SetExercise, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.apply {
            inflate(R.menu.exercise_menu)
            setOnMenuItemClickListener {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_delete_exercise, exercise.name))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            viewModel.deleteSetExercise(exercise)
                        }
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                true
            }
            show()
        }
    }

    override fun onSaveSetExerciseClick(setExercise: SetExercise) {
        viewModel.exerciseUpdated(setExercise)
    }

    private fun setupRecyclerView(exercisesList: List<SetExercise>) {
        trainingSetResultAdapter = TrainingSetResultAdapter(exercisesList)
        trainingSetResultAdapter?.setOnExerciseClicked { exercise ->
            viewModel.prepareNewExerciseDialog(exercise)
        }
        trainingSetResultAdapter?.setOnLongExerciseClick { exercise, view ->
            applyMenu(exercise, view)
        }
        trainingSetResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainingSetResultAdapter
        }
    }

    private fun showDialog(exercise: SetExercise?, exercisesList: List<ExerciseEntity>) {
        val dialog = TrainingSetResultDialog(exercise, exercisesList, this)
        dialog.setId = args.setId
        dialog.show(requireActivity().supportFragmentManager, DIALOG_TAG)
    }

}