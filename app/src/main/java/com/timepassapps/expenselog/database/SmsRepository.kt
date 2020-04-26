package com.timepassapps.expenselog.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.timepassapps.expenselog.models.Expense
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SmsRepository(context : Context) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Default
    private val expenseDao : ExpenseDao
    private lateinit var expensesList : LiveData<MutableList<Expense>>

    init {
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseDao = expenseDatabase.expenseDao()
    }

    suspend fun insertExpenses(expenses: List<Expense>) = withContext(Dispatchers.IO) {
        expenseDao.insertExpenseList(expenses)
    }

    suspend fun insertExpense(expenses : Expense) = withContext(Dispatchers.IO) {
        expenseDao.insertExpense(expenses)
    }

    suspend fun getExpenseList() : LiveData<MutableList<Expense>> = withContext(Dispatchers.IO) {
        if(!::expensesList.isInitialized) {
            expensesList = expenseDao.getAllExpenses()
        }
        return@withContext expensesList
    }

//    suspend fun getExpensesAfter(timestamp : Long) : LiveData<List<Expense>> = withContext(Dispatchers.IO) {
//        expenseDao.getExpensesAfter(timestamp)
//    }
}