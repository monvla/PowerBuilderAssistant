package com.monvla.powerbuilderassistant.ui.dairy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.adapters.DairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.Screen
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.android.synthetic.main.screen_training_dairy.*

class TrainingDairyFragment : Screen(), DairyRecordAdapter.ItemClick {

    private val viewModel: TrainingViewModel by activityViewModels()
    private lateinit var adapter: DairyRecordAdapter

    init {
        screenLayout = R.layout.screen_training_dairy
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DairyRecordAdapter(ArrayList())
        adapter.callback = this
        training_records_list.adapter = adapter
        setTitle(R.string.screen_training_dairy_name)

        viewModel.trainingRecords.observe(viewLifecycleOwner) { item ->
            adapter.updateData(item)
        }
        training_records_list.layoutManager = LinearLayoutManager(requireContext())

        add_record_fab.setOnClickListener {
//            val action = TrainingDairyFragmentDirections.actionScreenTrainingDairyToScreenDairyRecordDetails()
            val action = TrainingDairyFragmentDirections.actionScreenTrainingDairyToScreenRealTimeTraining()
            this.findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateTrainingData()
    }

    override fun onItemClicked(training: TrainingRecordEntity) {
        val action = TrainingDairyFragmentDirections.actionScreenTrainingDairyToScreenDairyRecordDetails(training.id)
        this.findNavController().navigate(action)
    }
}