package com.monvla.powerbuilderassistant.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.realtimetraining.SetResultDialogFragment
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.add_set_list_item.view.*

class SetResultDialogAdapter(
    private val trainingSetData: MutableList<SetResultDialogFragment.TrainingSetData>,
    val exercisesList: List<ExerciseEntity>,
    val dialogFragment: SetResultDialogFragment?
) :
    RecyclerView.Adapter<SetResultDialogAdapter.SetResultDialogViewHolder>() {

    var spinnerAdapter: ArrayAdapter<String>? = null

    class SetResultDialogViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SetResultDialogViewHolder {
        val viewGroup = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_set_list_item, parent, false) as ViewGroup
        return SetResultDialogViewHolder(viewGroup)
    }

    override fun onBindViewHolder(holder: SetResultDialogViewHolder, position: Int) {
        holder.viewGroup.context?.let { context ->
            if (spinnerAdapter == null) {
                val values = exercisesList.map { it.name }
                spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, values)
                spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            holder.viewGroup.exercisesSpinner.apply {
                adapter = spinnerAdapter
                setSelectionByName(trainingSetData[position].name)
            }
            holder.viewGroup.repeatsNumber.text = SpannableStringBuilder(trainingSetData[position].repeats.toString())
            holder.viewGroup.weight.text = SpannableStringBuilder(trainingSetData[position].weight.toString())
            // keyboard show kostyl
            dialogFragment?.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            dialogFragment?.dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun getItemCount() = trainingSetData.size

    fun Spinner.setSelectionByName(name: String) {
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == name) {
                setSelection(i)
            }
        }
    }
}