package com.example.myapplication.helper

import android.content.SharedPreferences
import com.example.myapplication.util.Constants.FIRST_LOGGED_IN_APP
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(
    private val sharedPref: SharedPreferences,
) {
    private fun getBooleanValue(): Boolean {
        return sharedPref.getBoolean(FIRST_LOGGED_IN_APP, true)
    }

    private fun changeBooleanValueToFalse() {
        val editor = sharedPref.edit()
        editor.putBoolean(FIRST_LOGGED_IN_APP, false).apply()
    }

    fun checkIfFirstAppOpened(): Boolean {
        val isFirstLogApp = getBooleanValue()
        if (isFirstLogApp)
            changeBooleanValueToFalse()
        return isFirstLogApp
    }
}