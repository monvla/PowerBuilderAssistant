package com.monvla.powerbuilderassistant

import android.os.Bundle

interface NavigationRoot {
    fun setBottomNavigationVisible(isVisible: Boolean)
    fun setHomeAsUpEnabled(enabled: Boolean)
    fun finishFragment()
    fun navigate(from: Class<*>, to: Class<*>, args: Bundle? = null)
    fun isTrainingInProgress(): Boolean
}