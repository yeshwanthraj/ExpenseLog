package com.timepassapps.expenselog.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.timepassapps.expenselog.utils.AppConstants
import com.timepassapps.expenselog.utils.SingletonHolder

class SharedPrefs private constructor(context: Context) {

    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

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