package com.monvla.powerbuilderassistant.ui.dairy

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.DairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.BottomNavigationFragment
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.android.synthetic.main.screen_training_dairy.*
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_main.*

class TrainingDairyFragment : BottomNavigationFragment(), DairyRecordAdapter.ItemClick {

    companion object {
        const val DESTINATION_EXERCISE_EDIT = 1
        const val DESTINATION_EXERCISE_STATISTICS = 2
    }

    private val viewModel: TrainingViewModel by activityViewModels()
    private lateinit var adapter: DairyRecordAdapter

    init {
        screenLayout = R.layout.screen_training_dairy
    }

    data class CalendarIndicator(override val color: Int, override val date: CalendarDate) : CalendarView.DateIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = DairyRecordAdapter(ArrayList())
        adapter.callback = this
        training_records_list.adapter = adapter
        setTitle(R.string.screen_training_dairy_name)
        viewModel.allTrainings.observe(viewLifecycleOwner) {
            setupCalendar(it)
        }
        viewModel.trainingRecords.observe(viewLifecycleOwner) { trainingsList ->
            adapter.updateData(trainingsList)
        }
        training_records_list.layoutManager = LinearLayoutManager(requireContext())

        add_record_fab.setOnClickListener {
            val action = TrainingDairyFragmentDirections.actionTrainingDairyFragmentToScreenRealTimeTraining()
            this.findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.dateSelected(Utils.getDateOnlyTimestamp(Date().time))
    }

    override fun onItemClicked(exercise: TrainingRecordEntity) {
        val action = TrainingDairyFragmentDirections.actionTrainingDairyFragmentToTrainingDetailsFragment(exercise.id)
        this.findNavController().navigate(action)
    }

    private fun setupCalendar(trainingRecordsList: List<TrainingRecordEntity>) {
        val calendar = Calendar.getInstance()
        val initialDate = CalendarDate(calendar.time)

        calendar.set(1900, 1, 1)
        val minDate = CalendarDate(calendar.time)

        calendar.set(2100, 1, 1)
        val maxDate = CalendarDate(calendar.time)

        val indicatorsList = mutableListOf<CalendarIndicator>()
        val displayedDates = mutableListOf<Long>()

        trainingRecordsList.forEach { trainingRecord ->
            val dateTimestamp = Utils.getDateOnlyTimestamp(trainingRecord.date)
            val strengthTrainingColor = resources.getColor(R.color.colorSecondaryLight)
            if (!displayedDates.contains(dateTimestamp)) {
                indicatorsList.add(
                    CalendarIndicator(strengthTrainingColor, CalendarDate(dateTimestamp))
                )
                displayedDates.add(dateTimestamp)
            }
        }

        calendarView.apply {
            datesIndicators = indicatorsList
            setupCalendar(
                initialDate = initialDate,
                minDate = minDate,
                maxDate = maxDate,
                selectionMode = CalendarView.SelectionMode.SINGLE,
                selectedDates = listOf(CalendarDate(Date().time)),
                firstDayOfWeek = Calendar.MONDAY,
                showYearSelectionView = false
            )

            onDateClickListener = {
                viewModel.dateSelected(it.date.time)
            }
        }
    }
}