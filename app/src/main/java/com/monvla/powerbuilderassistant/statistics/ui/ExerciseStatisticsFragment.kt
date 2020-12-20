package com.monvla.powerbuilderassistant.statistics.ui

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.Utils
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_exercise_statistics.*
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.model.Viewport
import java.util.*


class ExerciseStatisticsFragment() : Screen() {

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.statisticsData.observe(viewLifecycleOwner) {
            Log.d("LUPA","Data: $it")
            val axisData = mutableListOf<String>()
            val yAxisData = mutableListOf<Int>()
            it.forEach { data ->
                axisData.add(Utils.getFormattedDate(data.date))
                yAxisData.add(data.repeats)
            }
            createStatistics(axisData, yAxisData)
        }
    }

    private fun createStatistics(axisData: List<String>, yAxisData: List<Int>) {
        val yAxisValues = ArrayList<PointValue>()
        val axisValues = ArrayList<AxisValue>()
        val line = Line(yAxisValues)
        for (i in 0 until axisData.size) {
            axisValues.add(i, AxisValue(i.toFloat()).setLabel(axisData[i]))
        }

        for (i in 0 until yAxisData.size) {
            yAxisValues.add(PointValue(i.toFloat(), yAxisData[i].toFloat()))
        }
        val lines = ArrayList<Line>()
        lines.add(line)
        val data = LineChartData()
        data.lines = lines

        val axis = Axis()
        axis.values = axisValues
        data.axisXBottom = axis

        val yAxis = Axis()
        data.axisYLeft = yAxis

        line.color = Color.parseColor("#9C27B0")
        axis.textSize = 16;

        axis.textColor = Color.parseColor("#03A9F4");
        yAxis.textColor = Color.parseColor("#03A9F4");
        yAxis.textSize = 16;
        yAxis.name = getString(R.string.statistics_repeats);

        chart.lineChartData = data

        val viewport = Viewport(chart.maximumViewport)
        viewport.top = 130f
        viewport.bottom = 0f
        chart.maximumViewport = viewport
        chart.currentViewport = viewport
    }
}

class ExerciseStatisticsViewModelFactory(val application: Application, val exerciseId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExerciseStatisticsViewModel(application, exerciseId) as T
    }
}