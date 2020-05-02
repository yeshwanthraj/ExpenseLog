package com.timepassapps.expenselog.viewmodels.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timepassapps.expenselog.viewmodels.ScanViewModel

class ScanViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            return modelClass.getConstructor(Int::class.java)
                    .newInstance(context)
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}