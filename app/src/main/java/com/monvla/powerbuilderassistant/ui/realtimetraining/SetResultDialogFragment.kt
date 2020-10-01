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
    private lateinit var viewManager: RecyclerView.LayoutManager

    interface SetResultDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<TrainingSetData>)
    }

    val adapterValues = mutableListOf<TrainingSetData>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let { activity ->

        viewManager = LinearLayoutManager(activity)
        viewAdapter = SetResultDialogAdapter(adapterValues, exercisesList)

        val view = activity.layoutInflater.inflate(R.layout.layout_dialog_add_set, null) as ViewGroup

        recyclerView = view.add_set_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        view.addSetButton.setOnClickListener {button ->
            adapterValues.add(
                TrainingSetData(exercisesList[0].name, 0))
            viewAdapter.notifyDataSetChanged()
        }

        val builder = AlertDialog.Builder(activity)
        isCancelable = false

        builder.apply {
            setView(view)
            setPositiveButton(R.string.ok
            ) { dialog, id ->
                val setArray = mutableListOf<TrainingSetData>()
                adapterValues.forEachIndexed { index, _ ->
                    val viewGroup = recyclerView.getChildAt(index)
//                    val name = viewGroup.spinner.selectedItem as String
//                    val count = viewGroup.editTextNumberDecimal.text.toString().toInt()
//                    setArray.add(TrainingSetData(name, count))
                }
                listener.onDialogPositiveClick(this@SetResultDialogFragment, setArray)

            }
        }
        builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    data class TrainingSetData(val name: String, val repeats: Int)

}