package com.monvla.powerbuilderassistant.ui.dairy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.DairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_training_dairy.*
import kotlinx.coroutines.launch

class TrainingDairyFragment : Screen() {

    private val viewModel: TrainingViewModel by activityViewModels()

    init {
        screenLayout = R.layout.screen_training_dairy
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        training_records_list.adapter = DairyRecordAdapter(ArrayList())
        setTitle(R.string.screen_training_dairy_name)

        viewModel.trainingRecords.observe(viewLifecycleOwner) { item ->
            training_records_list.adapter = DairyRecordAdapter(item)
            (training_records_list.adapter as DairyRecordAdapter).notifyDataSetChanged()
        }
        training_records_list.layoutManager = LinearLayoutManager(requireContext())

        add_record_fab.setOnClickListener {
            val action =
                    TrainingDairyFragmentDirections.actionScreenTrainingDairyToScreenDairyCreateRecord()
            this.findNavController().navigate(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.clearAll()
        }
    }
}