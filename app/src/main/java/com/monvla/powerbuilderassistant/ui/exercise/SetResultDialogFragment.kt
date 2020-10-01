package com.monvla.powerbuilderassistant.ui.exercise

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.ui.record.DairyRecordViewModel
import com.monvla.powerbuilderassistant.vo.ExerciseEntity
import kotlinx.android.synthetic.main.add_set_list_item.view.*
import kotlinx.android.synthetic.main.layout_dialog_add_set.view.*
import java.lang.IllegalStateException

class SetResultDialogFragment : DialogFragment() {

    internal lateinit var listener: SetResultDialogListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var exercisesList: List<ExerciseEntity>? = null
    var nextTrainingId: Long? = null

    interface SetResultDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, data: MutableList<SetData>)
    }

    val adapterValues = mutableListOf("1")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let { activity ->

        viewManager = LinearLayoutManager(activity)
        exercisesList?.let {
            viewAdapter = MyAdapter(adapterValues, it)
        }

        val view = activity.layoutInflater.inflate(R.layout.layout_dialog_add_set, null) as ViewGroup

        recyclerView = view.add_set_recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        view.addSetButton.setOnClickListener {button ->
            Toast.makeText(activity, "LUPA", Toast.LENGTH_SHORT).show()
            adapterValues.add("2")
            viewAdapter.notifyDataSetChanged()
        }

        val builder = AlertDialog.Builder(activity)
        isCancelable = false

        builder.apply {
            setView(view)
            setPositiveButton(R.string.ok
            ) { dialog, id ->
                val setArray = mutableListOf<SetData>()
                adapterValues.forEachIndexed { index, _ ->
                    val viewGroup = recyclerView.getChildAt(index)
                    val name = viewGroup.spinner.selectedItem as String
                    val count = viewGroup.editTextNumberDecimal.text.toString().toInt()
                    setArray.add(SetData(name, count))
                }
                listener.onDialogPositiveClick(this@SetResultDialogFragment, setArray)

                val i = nextTrainingId
            }
        }
        builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")

    class MyAdapter(private val myDataset: MutableList<String>, val exercisesList: List<ExerciseEntity>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var adapter: ArrayAdapter<String>? = null

        class MyViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.MyViewHolder {
            val viewGroup = LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_set_list_item, parent, false) as ViewGroup
            return MyViewHolder(viewGroup)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.viewGroup.context?.let {context ->
                if (adapter == null) {
                    val values = exercisesList.map { it.name }
                    adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, values)
                    adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                val spinner = holder.viewGroup.findViewById<Spinner>(R.id.spinner)
                spinner.adapter = adapter
            }
        }
        override fun getItemCount() = myDataset.size
    }

    data class SetData(val name: String, val repeats: Int)

}