package com.monvla.powerbuilderassistant.ui.settings

import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import com.monvla.powerbuilderassistant.BuildConfig
import com.monvla.powerbuilderassistant.R

class SettingsFragment : PreferenceScreen(R.xml.preferences) {

    companion object {
        const val ABOUT_KEY = "about"
        const val EDIT_EXERCISES_KEY = "edit_exercises"
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
                navigationRoot.navigate(this.javaClass, ExercisesListFragment::class.java)
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}