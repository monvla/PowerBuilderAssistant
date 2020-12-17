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
        viewModel.data.observe(viewLifecycleOwner) {
            Log.d("LUPA","Data: $it")
        }
    }

    private fun createStatistics() {
        val axisData = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
            "Oct", "Nov", "Dec"
        )
        val yAxisData = intArrayOf(50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18)

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
        axis.setValues(axisValues)
        data.axisXBottom = axis

        val yAxis = Axis()
        data.axisYLeft = yAxis

        line.setColor(Color.parseColor("#9C27B0"))
        axis.setTextSize(16);

        axis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        yAxis.setName("Sales in millions");

        chart.lineChartData = data

        val viewport = Viewport(chart.getMaximumViewport())
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