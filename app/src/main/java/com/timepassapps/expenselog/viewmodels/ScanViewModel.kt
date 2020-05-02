package com.timepassapps.expenselog.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.timepassapps.expenselog.data.database.Expense
import com.timepassapps.expenselog.data.ExpenseRepository
import com.timepassapps.expenselog.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ScanViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val TAG = "SmsViewModel"

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val expenseRepository = ExpenseRepository(application)

    private lateinit var expenseList : LiveData<MutableList<Expense>>


    suspend fun getExpenseList() : LiveData<MutableList<Expense>> {
        if(!::expenseList.isInitialized) {
            expenseList = expenseRepository.getExpenseList()
        }

        return expenseList
    }

    suspend fun insertExpense(expense: Expense) {
        expenseRepository.insertExpense(expense)
    }

    suspend fun insertExpenses(expenses : List<Expense>) {
        expenseRepository.insertExpenses(expenses)
    }

    suspend fun generateExpenses() = withContext(Dispatchers.IO) {
        val cursor = getApplication<Application>().contentResolver.query(Uri.parse(SmsConstants.SMS_CONTENT_URI), null, null, null, null)
        return@withContext cursor?.let {
            val list = ArrayList<Expense>()
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
                do {
                    if (senderColumnIndex != -1 && isBankSms(cursor.getString(senderColumnIndex))) {
                        if (bodyColumnIndex != -1) {
                            val expense = Expense()
                            expense.bank = getBankName(cursor.getString(senderColumnIndex))
                            val smsMessage = cursor.getString(bodyColumnIndex)
                            expense.message = smsMessage
                            expense.amount = getExpenseAmount(smsMessage)
                            expense.time = cursor.getLong(timeColumnIndex)
                            if (hasBalance(cursor.getString(bodyColumnIndex))) {
                                expense.balance = getBalance(smsMessage)
                            }
                            list.add(expense)
                        }
                    }
                } while (cursor.moveToNext())
                cursor.close()
                Log.d(TAG, "generateExpenseList: expense list size is " + list.size)
            }
            list
        }
    }

}