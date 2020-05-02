package com.timepassapps.expenselog.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ExpenseDao {

    @Insert
    fun insertExpense(expense: Expense)

    @Insert
    fun insertExpenseList(expenses: List<Expense>)

    @Delete
    fun delete(expense: Expense)

    @Query("select * from Expense")
    fun getAllExpenses() : LiveData<MutableList<Expense>>

//    @Query("select * from Expense where time >= :timeStamp")
//    fun getExpensesAfter(timeStamp : Long) : LiveData<List<Expense>>

}