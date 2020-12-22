package com.monvla.powerbuilderassistant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.monvla.powerbuilderassistant.MainActivity

open class Screen : Fragment() {

    var screenLayout: Int = 0

    fun setTitle(stringId: Int) {
        (requireContext() as MainActivity).supportActionBar?.title = getString(stringId)
    }

    fun setTitle(string: String) {
        (requireContext() as MainActivity).supportActionBar?.title = string
    }

    fun clearTitle() {
        (requireContext() as MainActivity).supportActionBar?.title = null
    }

    fun setUpButtonEnabled(state: Boolean) {
        (requireContext() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(state);
        (requireContext() as MainActivity).supportActionBar?.setDisplayShowHomeEnabled(state);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpButtonEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(screenLayout, container, false)
}