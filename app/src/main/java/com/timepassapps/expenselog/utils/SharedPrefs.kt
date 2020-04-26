package com.timepassapps.expenselog.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs private constructor(context: Context) {

    private val sharedPrefs = context.getSharedPreferences(AppConstants.APP_PREFS,Context.MODE_PRIVATE)

    private val sharedPrefsEditor : SharedPreferences.Editor

    companion object : SingletonHolder<SharedPrefs, Context>(::SharedPrefs)

    init {
        sharedPrefsEditor = sharedPrefs.edit()
    }

    fun getString(key : String,defaultValue : String) =
            sharedPrefs.getString(key,defaultValue)

    fun getBoolean(key : String, defaultValue: Boolean) : Boolean =
            sharedPrefs.getBoolean(key,defaultValue)

    fun putBoolean(key: String, value : Boolean) =
            sharedPrefsEditor.putBoolean(key,value).apply()
}