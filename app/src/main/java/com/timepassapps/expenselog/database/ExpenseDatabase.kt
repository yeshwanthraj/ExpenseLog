package com.timepassapps.expenselog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.timepassapps.expenselog.models.Expense
import com.timepassapps.expenselog.utils.CurrencyTypeConverter


@Database(entities = [Expense::class], version = 1)
@TypeConverters(CurrencyTypeConverter::class)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {

        private var instance : ExpenseDatabase? = null

        @Synchronized
        fun getInstance(context : Context) : ExpenseDatabase{
            if(instance == null) {
                instance = Room
                        .databaseBuilder(context,ExpenseDatabase::class.java,"expense")
                        .build()
            }
            return instance!!
        }
    }
}

