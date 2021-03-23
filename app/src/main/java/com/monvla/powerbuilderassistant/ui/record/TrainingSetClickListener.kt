package com.monvla.powerbuilderassistant.ui.record

import android.view.ViewGroup

interface TrainingSetClickListener {
    fun onSetClick(setNumber: Int, setId: Long)
    fun onLongSetClick(setId: Long, setViewGroup: ViewGroup)
}