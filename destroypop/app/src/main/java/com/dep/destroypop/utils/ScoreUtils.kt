package com.dep.destroypop.utils

import android.content.Context
import android.content.SharedPreferences

object ScoreUtils {

    private const val PREFS_GLOBAL = "prefs_global"
    private const val PREF_TOP_SCORE = "pref_top_score"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_GLOBAL, Context.MODE_PRIVATE)
    }

    fun getTopScore(context: Context): Int {
        return getPreferences(context).getInt(PREF_TOP_SCORE, 0)
    }

}