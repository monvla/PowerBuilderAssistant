package com.monvla.powerbuilderassistant.ui.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.monvla.powerbuilderassistant.vo.TrainingRecord
import kotlin.collections.ArrayList

class DairyCreateRecordViewModel : ViewModel() {
    val selected = MutableLiveData<ArrayList<TrainingRecord>>()
}

