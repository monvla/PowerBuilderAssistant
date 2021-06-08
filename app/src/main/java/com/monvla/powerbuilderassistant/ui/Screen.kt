package com.monvla.powerbuilderassistant.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.monvla.powerbuilderassistant.MainActivity
import com.monvla.powerbuilderassistant.NavigationRoot

open class Screen : Fragment() {

    var screenLayout: Int = 0
    lateinit var navigationRoot: NavigationRoot

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationRoot = requireActivity() as NavigationRoot
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