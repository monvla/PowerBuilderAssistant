package com.monvla.powerbuilderassistant.ui.dairy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.adapters.DairyRecordAdapter
import com.monvla.powerbuilderassistant.ui.BottomNavigationFragment
import com.monvla.powerbuilderassistant.ui.record.TrainingDetailsFragment
import com.monvla.powerbuilderassistant.vo.TrainingRecordEntity
import kotlinx.android.synthetic.main.screen_training_dairy.*
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import java.util.*
import kotlin.collections.ArrayList

class TrainingDairyFragment : BottomNavigationFragment(), DairyRecordAdapter.ItemClick {

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
        viewModel.allTrainings.observe(viewLifecycleOwner) {
            setupCalendar(it)
        }
        viewModel.trainingRecords.observe(viewLifecycleOwner) { trainingsList ->
            adapter.updateData(trainingsList)
        }
        training_records_list.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        viewModel.dateSelected(Utils.getDateOnlyTimestamp(Date().time))
    }

    override fun onItemClicked(training: TrainingRecordEntity) {
        val args = Bundle().also {
            it.putLong(TrainingDetailsFragment.KEY_TRAINING_ID, training.id)
        }
        navigationRoot.navigate(this.javaClass, TrainingDetailsFragment::class.java, args)
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