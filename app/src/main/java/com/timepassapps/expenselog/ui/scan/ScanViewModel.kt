package com.timepassapps.expenselog.ui.scan

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.timepassapps.expenselog.data.database.Expense
import com.timepassapps.expenselog.data.ExpenseRepository
import com.timepassapps.expenselog.utils.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ScanViewModel(application: Application,private val expenseRepository: ExpenseRepository) : AndroidViewModel(application), CoroutineScope {

	private val TAG = "SmsViewModel"

	private val job = Job()

	override val coroutineContext: CoroutineContext
		get() = job + Dispatchers.Main

	val isExpenseDbGenerated = MutableLiveData<Boolean>()

	fun generateExpenseDb() {
		launch {
			val expenseList = getExpensesFromSmsDb()
			expenseList?.run {
				expenseRepository.insertExpenses(expenseList)
			}
		}.invokeOnCompletion {
			isExpenseDbGenerated.value = true
		}
	}

	private suspend fun getExpensesFromSmsDb(): List<Expense>? = withContext(Dispatchers.IO) {
		val cursor = getApplication<Application>().contentResolver.query(Uri.parse(SmsConstants.SMS_CONTENT_URI), null, null, null, null)
		cursor?.let {
			if (cursor.moveToFirst()) { // must check the result to prevent exception
				val columnNames = ArrayList(listOf(*cursor.columnNames))
				columnNames.sort()
				var senderColumnIndex = -1
				var bodyColumnIndex = -1
				var timeColumnIndex = -1
				if (columnNames.contains(SmsConstants.SENDER_COLUMN)) {
					senderColumnIndex = cursor.getColumnIndex(SmsConstants.SENDER_COLUMN)
				}
				if (columnNames.contains(SmsConstants.BODY_COLUMN)) {
					bodyColumnIndex = cursor.getColumnIndex(SmsConstants.BODY_COLUMN)
				}
				if (columnNames.contains(SmsConstants.TIME_COLUMN)) {
					timeColumnIndex = cursor.getColumnIndex(SmsConstants.TIME_COLUMN)
				}
				val list = ArrayList<Expense>()
				do {
					if (senderColumnIndex != -1 && bodyColumnIndex != -1) {
						val messageBody = cursor.getString(bodyColumnIndex)
						val sender = cursor.getString(senderColumnIndex)
						if (isBankSms(sender) && isDebit(messageBody)) {
							val expense = Expense()
							expense.bank = getBankName(sender)
							expense.message = messageBody
							expense.amount = getExpenseAmount(messageBody)
							expense.time = cursor.getLong(timeColumnIndex)
							if (hasBalance(cursor.getString(bodyColumnIndex))) {
								expense.balance = getBalance(messageBody)
							}
							list.add(expense)
							Log.d(TAG, "Expense for sms ${expense.message} is ${expense.amount}")
						}
					} else {
						return@withContext null
					}
				} while (cursor.moveToNext())
				cursor.close()
				Log.d(TAG, "generateExpenseList: expense list size is " + list.size)
				return@withContext list
			}
			return@withContext null
		}
		return@withContext null
	}
}