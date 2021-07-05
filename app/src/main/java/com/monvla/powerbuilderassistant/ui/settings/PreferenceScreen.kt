package com.monvla.powerbuilderassistant.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.monvla.powerbuilderassistant.NavigationRoot

open class PreferenceScreen(@XmlRes val preferenceXml: Int) : PreferenceFragmentCompat() {

    lateinit var navigationRoot: NavigationRoot

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferenceXml, rootKey)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigationRoot = requireActivity() as NavigationRoot
    }
}