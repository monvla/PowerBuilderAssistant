package com.monvla.powerbuilderassistant.statistics.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.monvla.powerbuilderassistant.R
import com.monvla.powerbuilderassistant.ui.Screen
import kotlinx.android.synthetic.main.screen_exercise_statistics.*


class ExerciseStatisticsFragment() : Screen(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    init {
        screenLayout = R.layout.screen_exercise_statistics
    }

    private lateinit var chart: LineChart
    private lateinit var seekBarX: SeekBar
    private lateinit var seekBarY:SeekBar
    private lateinit var tvY:TextView
    private lateinit var tvX:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvX = tvXMax
        tvY = tvYMax

        seekBarX = seekBar1
        seekBarX!!.setOnSeekBarChangeListener(this)

        seekBarY = seekBar2
        seekBarY!!.setOnSeekBarChangeListener(this)

        chart = chart1
        chart!!.setOnChartValueSelectedListener(this)

        // no description text

        // no description text
        chart!!.description.isEnabled = false

        // enable touch gestures

        // enable touch gestures
        chart!!.setTouchEnabled(true)

        chart!!.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging

        // enable scaling and dragging
        chart!!.isDragEnabled = true
        chart!!.setScaleEnabled(true)
        chart!!.setDrawGridBackground(false)
        chart!!.isHighlightPerDragEnabled = true

        // if disabled, scaling can be done on x- and y-axis separately

        // if disabled, scaling can be done on x- and y-axis separately
        chart!!.setPinchZoom(true)

        // set an alternative background color

        // set an alternative background color
        chart!!.setBackgroundColor(Color.LTGRAY)

        // add data

        // add data
        seekBarX!!.progress = 20
        seekBarY!!.progress = 30

        chart!!.animateX(1500)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l = chart!!.legend

        // modify the legend ...

        // modify the legend ...
        l.form = LegendForm.LINE
//        l.typeface = tfLight
        l.textSize = 11f
        l.textColor = Color.WHITE
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
//        l.setYOffset(11f);

        //        l.setYOffset(11f);
        val xAxis = chart!!.xAxis
//        xAxis.typeface = tfLight
        xAxis.textSize = 11f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis = chart!!.axisLeft
//        leftAxis.typeface = tfLight
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.axisMaximum = 200f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true

        val rightAxis = chart!!.axisRight
//        rightAxis.typeface = tfLight
        rightAxis.textColor = Color.RED
        rightAxis.axisMaximum = 900f
        rightAxis.axisMinimum = -200f
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false
    }

    fun setData(count: Int, range: Float) {

        val values1 = ArrayList<Entry>();

        for (i in 0..count) {
            val temp = (Math.random() * (range / 2f)) + 50
            values1.add(Entry(i.toFloat(), temp.toFloat()))
        }

        val values2 = ArrayList<Entry>();

        for (i in 0..count) {
            val temp = (Math.random() * range) + 450;
            values2.add(Entry(i.toFloat(), temp.toFloat()))
        }

        val values3 = ArrayList<Entry>();

        for (i in 0..count) {
            val temp = (Math.random() * range) + 500;
            values3.add(Entry(i.toFloat(), temp.toFloat()))
        }

        if (chart.getData() != null &&
            chart.getData().getDataSetCount() > 0) {
            val set1 = chart.getData().getDataSetByIndex(0) as LineDataSet
            val set2 = chart.getData().getDataSetByIndex(1) as LineDataSet
            val set3 = chart.getData().getDataSetByIndex(2) as LineDataSet
            set1.setValues(values1);
            set2.setValues(values2);
            set3.setValues(values3);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            val set1 = LineDataSet(values1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            val set2 = LineDataSet(values2, "DataSet 2");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            val set3 = LineDataSet(values3, "DataSet 3");
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setColor(Color.YELLOW);
            set3.setCircleColor(Color.WHITE);
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
            set3.setDrawCircleHole(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the data sets
            val data = LineData(set1, set2, set3);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            chart.setData(data);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.line, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.getItemId()) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/LineChartActivity2.java"));
                startActivity(i);
            }
            R.id.actionToggleValues -> {
                chart.getData().getDataSets().forEach { iSet ->
                    iSet.setDrawValues(!iSet.isDrawValuesEnabled());
                }
                chart.invalidate();
            }
            R.id.actionToggleHighlight -> {
                if (chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
            }
            R.id.actionToggleFilled -> {
                chart.getData().getDataSets().forEach {set ->
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                chart.invalidate();
            }
            R.id.actionToggleCircles -> {
                chart.getData().getDataSets().forEach {set: ILineDataSet ->
                    if (set.isDrawCirclesEnabled())
                        (set as LineDataSet).setDrawCircles(false);
                    else
                        (set as LineDataSet).setDrawCircles(true);
                }
                chart.invalidate();
            }
            R.id.actionToggleCubic -> {
                chart.getData().getDataSets().forEach { set->
                    (set as LineDataSet).setMode(
                        if (set.getMode() == LineDataSet.Mode.CUBIC_BEZIER) {
                            LineDataSet.Mode.LINEAR
                        } else {
                            LineDataSet.Mode.CUBIC_BEZIER
                        }
                    )
                }
                chart.invalidate()
            }
            R.id.actionToggleStepped -> {
                chart.getData().getDataSets().forEach {set ->
                    (set as LineDataSet).setMode(
                        if (set.getMode() == LineDataSet.Mode.STEPPED) {
                            LineDataSet.Mode.LINEAR
                        } else {
                            LineDataSet.Mode.STEPPED
                        }
                    )
                }
                chart.invalidate();
            }
            R.id.actionToggleHorizontalCubic -> {
                chart.getData().getDataSets().forEach {set ->
                    (set as LineDataSet).setMode(
                        if (set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER) {
                            LineDataSet.Mode.LINEAR
                        } else {
                            LineDataSet.Mode.HORIZONTAL_BEZIER
                        }
                    )
                }
                chart.invalidate();
            }
            R.id.actionTogglePinch -> {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
            }
            R.id.animateX -> {
                chart.animateX(2000);
            }
            R.id.animateY -> {
                chart.animateY(2000);
            }
            R.id.animateXY -> {
                chart.animateXY(2000, 2000);
            }
            R.id.actionSave -> {
//                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    saveToGallery();
//                } else {
//                    requestStoragePermission(chart);
//                }
            }
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        tvX.setText(seekBarX.getProgress().toString())
        tvY.setText(seekBarY.getProgress().toString())

        setData(seekBarX.getProgress(), seekBarY.getProgress().toFloat());

        chart.invalidate();
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        Log.i("LUPA Entry selected", e.toString());

        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    override fun onNothingSelected() {
        Log.i("LUPA Nothing selected", "Nothing selected.");
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}