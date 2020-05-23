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

    private lateinit var expenseList: LiveData<MutableList<Expense>>


    suspend fun getExpenseList(): LiveData<MutableList<Expense>> {
        if (!::expenseList.isInitialized) {
            expenseList = expenseRepository.getExpenseList()
        }

        return expenseList
    }

    suspend fun insertExpense(expense: Expense) {
        expenseRepository.insertExpense(expense)
    }

    suspend fun insertExpenses(expenses: List<Expense>) {
        expenseRepository.insertExpenses(expenses)
    }

    suspend fun generateExpenses(): List<Expense>? = withContext(Dispatchers.IO) {
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