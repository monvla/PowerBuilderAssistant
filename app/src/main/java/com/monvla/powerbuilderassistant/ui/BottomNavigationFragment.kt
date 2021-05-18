package com.monvla.powerbuilderassistant.ui

open class BottomNavigationFragment : Screen() {

    override fun onResume() {
        super.onResume()
        navigationRoot.setBottomNavigationVisible(true)
    }

}