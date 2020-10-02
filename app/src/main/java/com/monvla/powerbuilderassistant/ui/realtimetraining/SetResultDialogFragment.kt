package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.SetResultDialogAdapter
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.add_set_list_item.view.*
import kotlinx.android.synthetic.main.layout_dialog_add_set.view.*
import java.lang.IllegalStateException

class SetResultDialogFragment(var exercisesList: List<ExerciseEntity>) : DialogFragment() {

    internal lateinit var listener: SetResultDialogListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    interface SetResultDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<TrainingSetData>)
    }

    private val trainingSetData = mutableListOf(TrainingSetData(exercisesList[0].name, 0, 0.0f))

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let { activity ->

        viewAdapter = SetResultDialogAdapter(trainingSetData, exercisesList)

        val view = activity.layoutInflater.inflate(R.layout.layout_dialog_add_set, null) as ViewGroup

        recyclerView = view.add_set_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = viewAdapter
        }
        view.addSetButton.setOnClickListener {button ->
            trainingSetData.add(
                TrainingSetData(exercisesList[0].name, 0, 0.0f))
            updateTrainingSetData()
            viewAdapter.notifyDataSetChanged()
        }

        val builder = AlertDialog.Builder(activity)
        isCancelable = false

        builder.apply {
            setView(view)
            setPositiveButton(R.string.ok
            ) { _, _ ->
                updateTrainingSetData()
                listener.onDialogPositiveClick(this@SetResultDialogFragment, trainingSetData)
            }
        }
        builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    private fun updateTrainingSetData() {
        for (i in 0 until recyclerView.childCount) {
            val viewGroup = recyclerView.getChildAt(i)
            trainingSetData[i].name = viewGroup.exercisesSpinner.selectedItem as String
            trainingSetData[i].repeats = viewGroup.repeatsNumber.text.toString().toInt()
            trainingSetData[i].weight = viewGroup.weight.text.toString().toFloat()
        }
    }

    data class TrainingSetData(var name: String, var repeats: Int, var weight: Float)

}