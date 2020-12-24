package com.monvla.powerbuilderassistant.statistics.ui

import android.app.Application
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_exercise_statistics.*
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import java.lang.Math.min
import java.util.*

class ExerciseStatisticsFragment : Screen() {

    companion object {
        const val PAGE_LOADING = 0
        const val PAGE_STATISTICS = 1
        const val PAGE_NO_DATA = 2
    }

    init {
        screenLayout = R.layout.screen_exercise_statistics
    }

    data class DateLabel(val position: Int, val label: String)

    private val args: ExerciseStatisticsFragmentArgs by navArgs()
    private val datesList = ArrayList<AxisValue>()
    private val valuesList = ArrayList<PointValue>()

    private val viewModel: ExerciseStatisticsViewModel by viewModels {
        ExerciseStatisticsViewModelFactory(requireActivity().application, args.exerciseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        clearTitle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        statistics_view_flipper.displayedChild = PAGE_LOADING
        viewModel.exerciseName.observe(viewLifecycleOwner) {
            setTitle(it)
        }
        viewModel.statisticsData.observe(viewLifecycleOwner) {
            if (it.isEmpty() || it.size == 1) {
                statistics_view_flipper.displayedChild = PAGE_NO_DATA
            } else {
                statistics_view_flipper.displayedChild = PAGE_STATISTICS
                val axisDatesData = mutableListOf<DateLabel>()
                val axisValuesData = mutableListOf<Int>()
                var lastDate = ""
                it.forEachIndexed { i, data ->
                    val formattedDate = Utils.getFormattedMonth(data.date)
                    if (lastDate != formattedDate) {
                        lastDate = formattedDate
                        axisDatesData.add(DateLabel(i, formattedDate))
                    }
                    axisValuesData.add(data.repeats)
                }
                createStatistics(axisDatesData, axisValuesData)
            }
        }
    }

    private fun createStatistics(rawAxisDatesData: List<DateLabel>, rawAxisValuesData: List<Int>) {
        rawAxisDatesData.forEach { data -> datesList.add(AxisValue(data.position.toFloat()).setLabel(data.label)) }
        rawAxisValuesData.forEachIndexed{ i, data -> valuesList.add(PointValue(i.toFloat(), data.toFloat())) }

        val line = Line(valuesList).apply {
            isFilled = true
            setHasLabelsOnlyForSelected(true)
            color = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLine)
        }

        val dateAxis = Axis().apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLabels)
            textSize = 16
            name = getString(R.string.statistics_date_axis)
            values = datesList
        }

        val valuesAxis = Axis().apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLabels)
            textSize = 16
            name = getString(R.string.statistics_repeats)
            formatter = SimpleAxisValueFormatter(0)
        }
        chart.apply {
            lineChartData = LineChartData().apply {
                this.lines = listOf(line)
                axisYLeft = valuesAxis
                axisXBottom = dateAxis
                zoomType = ZoomType.HORIZONTAL
            }
        }
        updateViewport()
    }

    private fun updateViewport() {
        val viewport = Viewport(chart.maximumViewport).apply {
            val maxListValue = requireNotNull(valuesList.maxBy{it.y}).y
            top = maxOf(
                    (maxListValue * 2),
                getTopValue()
            )
            bottom = 0f
            left = 0f
        }
        chart.apply {
            maximumViewport = viewport
            currentViewport = viewport
//            setCurrentViewportWithAnimation(viewport)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateViewport()
    }

    private fun getTopValue(): Float = run {
        var topValue = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 20 else 11
        if (resources.getBoolean(R.bool.isTablet)) topValue *= 2
        return topValue.toFloat()
    }
}

class ExerciseStatisticsViewModelFactory(val application: Application, val exerciseId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExerciseStatisticsViewModel(application, exerciseId) as T
    }
}