package com.monvla.powerbuilderassistant.vo

data class SetExercise(
    var name: String,
    var repeats: Int,
    var weight: Float,
    var setId: Long? = null,
    var id: Long? = null
)