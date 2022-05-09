package com.example.sykkelapp

import android.content.Context
import android.content.SharedPreferences

class PrefManager internal constructor(var context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor

    private val MODE_PRIVATE = 0

    //call in intro activity to set false after first launch
    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
        set(isFirstLaunch) {
            editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch)
            editor.commit()
        }

    companion object {
        private const val PREF_NAME = "IntroScreen"
        private const val IS_FIRST_LAUNCH = "IsFirstLaunch"
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
}