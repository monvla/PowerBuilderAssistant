package com.monvla.powerbuilderassistant.ui

open class SimpleFragment : Screen() {
    override fun onResume() {
        super.onResume()
        navigationRoot.setBottomNavigationVisible(false)
    }
}