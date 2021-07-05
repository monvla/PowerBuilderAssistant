package com.monvla.powerbuilderassistant

import android.os.Bundle

interface NavigationRoot {
    fun setBottomNavigationVisible(isVisible: Boolean)
    fun navigate(from: Class<*>, to: Class<*>, args: Bundle? = null)
}