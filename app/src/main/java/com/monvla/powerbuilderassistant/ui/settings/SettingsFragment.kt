package com.monvla.powerbuilderassistant.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.monvla.powerbuilderassistant.BuildConfig
import com.monvla.powerbuilderassistant.R

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val ABOUT_KEY = "about"
        const val EDIT_EXERCISES_KEY = "edit_exercises"

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            ABOUT_KEY -> {
                val text = "${BuildConfig.VERSION_NAME} ${BuildConfig.FLAVOR}"
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getString(R.string.settings_about_title))
                    setMessage(text)
                }.show()
            }
            EDIT_EXERCISES_KEY -> {
                findNavController().navigate(R.id.action_settingsFragment_to_exercisesListFragment)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}