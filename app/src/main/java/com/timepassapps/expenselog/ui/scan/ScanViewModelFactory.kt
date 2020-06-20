package com.timepassapps.expenselog.ui.scan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timepassapps.expenselog.data.ExpenseRepository

class ScanViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ScanViewModel::class.java)) {
			return ScanViewModel(application,ExpenseRepository(application)) as T
		}
		throw IllegalArgumentException("Unknown view model class")
	}
}