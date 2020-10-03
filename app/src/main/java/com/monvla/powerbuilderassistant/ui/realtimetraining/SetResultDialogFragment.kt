package com.monvla.powerbuilderassistant.ui.realtimetraining

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.SetResultDialogAdapter
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.add_set_list_item.*
import kotlinx.android.synthetic.main.add_set_list_item.view.*
import kotlinx.android.synthetic.main.layout_dialog_add_set.view.*

class SetResultDialogFragment(var exercisesList: List<ExerciseEntity>) : DialogFragment() {

    internal lateinit var listener: SetResultDialogListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    interface SetResultDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<TrainingSetData>)
    }

    private val trainingSetData = mutableListOf(TrainingSetData(exercisesList[0].name, 0, 0.0f))

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        viewAdapter = SetResultDialogAdapter(trainingSetData, exercisesList, this)

        val view = activity!!.layoutInflater.inflate(R.layout.layout_dialog_add_set, null) as ViewGroup

        recyclerView = view.add_set_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        view.addSetButton.setOnClickListener { button ->
            trainingSetData.add(
                TrainingSetData(exercisesList[0].name, 0, 0.0f)
            )
            updateTrainingSetData()
            viewAdapter.notifyDataSetChanged()
        }

        retainInstance = true
        isCancelable = false

        builder.apply {
            setView(view)
            setPositiveButton(
                "Сохранить"
            ) { _, _ ->
                updateTrainingSetData()
                listener.onDialogPositiveClick(this@SetResultDialogFragment, trainingSetData)
            }
        }
        return builder.create()
    }

    private fun updateTrainingSetData() {
        for (i in 0 until recyclerView.childCount) {
            val viewGroup = recyclerView.getChildAt(i)
            val repeatsContent = viewGroup.repeatsNumber.text.toString()
            val weightContent = viewGroup.weight.text.toString()
            trainingSetData[i].name = viewGroup.exercisesSpinner.selectedItem as String
            trainingSetData[i].repeats = if (repeatsContent.isNotBlank()) repeatsContent.toInt() else 0
            trainingSetData[i].weight = if (weightContent.isNotBlank()) weightContent.toFloat() else 0f
        }
    }

    data class TrainingSetData(var name: String, var repeats: Int, var weight: Float)

}