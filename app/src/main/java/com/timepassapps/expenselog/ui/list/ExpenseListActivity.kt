package com.timepassapps.expenselog.ui.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.timepassapps.expenselog.R

import kotlinx.android.synthetic.main.activity_expense_list.*

class ExpenseListActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_expense_list)
		setSupportActionBar(toolbar)

	}

}
