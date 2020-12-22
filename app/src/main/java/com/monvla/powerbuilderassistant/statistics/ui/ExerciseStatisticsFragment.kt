package com.monvla.powerbuilderassistant.statistics.ui

import android.app.Application
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
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
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

    private val args: ExerciseStatisticsFragmentArgs by navArgs()

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
            if (it.isEmpty()) {
                statistics_view_flipper.displayedChild = PAGE_NO_DATA
            } else {
                statistics_view_flipper.displayedChild = PAGE_STATISTICS
                val axisDatesData = mutableListOf<String>()
                val axisValuesData = mutableListOf<Int>()
                var lastDate = ""
                it.forEach { data ->
                    val formattedDate = Utils.getFormattedMonth(data.date)
                    if (lastDate != formattedDate) {
                        lastDate = formattedDate
                        axisDatesData.add(formattedDate)
                    } else {
                        axisDatesData.add("")
                    }
                    axisValuesData.add(data.repeats)
                }
                createStatistics(axisDatesData, axisValuesData)
            }
        }
    }

    private fun createStatistics(rawAxisDatesData: List<String>, rawAxisValuesData: List<Int>) {
        val axisDatesData = ArrayList<PointValue>()
        val axisValuesData = ArrayList<AxisValue>()

        rawAxisDatesData.forEachIndexed { i, data -> axisValuesData.add(AxisValue(i.toFloat()).setLabel(data)) }
        rawAxisValuesData.forEachIndexed{ i, data -> axisDatesData.add(PointValue(i.toFloat(), data.toFloat())) }

        val line = Line(axisDatesData).apply {
            isFilled = true
            setHasLabelsOnlyForSelected(true)
            color = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLine)
        }

        val dateAxis = Axis().apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLabels)
            textSize = 16
            name = getString(R.string.statistics_date_axis)
            values = axisValuesData
        }

        val valuesAxis = Axis().apply {
            textColor = ContextCompat.getColor(requireContext(), R.color.colorStatisticsLabels)
            textSize = 16
            name = getString(R.string.statistics_repeats)
        }

        val viewport = Viewport(chart.maximumViewport).apply {
            top = (requireNotNull(rawAxisValuesData.max()) * 2).toFloat() - 1
            bottom = 0f
            left = 0f
            right = axisDatesData.size.toFloat() - 1
        }
        chart.apply {
            lineChartData = LineChartData().apply {
                this.lines = listOf(line)
                axisYLeft = valuesAxis
                axisXBottom = dateAxis
            }
            zoomType = ZoomType.HORIZONTAL
            maximumViewport = viewport
            currentViewport = viewport
//            setCurrentViewportWithAnimation(viewport)
        }
    }
}

class ExerciseStatisticsViewModelFactory(val application: Application, val exerciseId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExerciseStatisticsViewModel(application, exerciseId) as T
    }
}