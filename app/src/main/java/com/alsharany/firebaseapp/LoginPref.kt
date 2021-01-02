package com.alsharany.firebaseapp

import android.content.Context
import android.preference.PreferenceManager

const val PREF_ACCOUNT_CHECK = ""
object LoginPref {
    fun getStoredQuery(context: Context): String {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_ACCOUNT_CHECK, "")!!
    }

    fun setStoredQuery(context: Context, state: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(PREF_ACCOUNT_CHECK, state).apply()
    }
}