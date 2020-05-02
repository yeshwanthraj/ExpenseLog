package com.timepassapps.expenselog.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.timepassapps.expenselog.data.database.ExpenseDatabase
import com.timepassapps.expenselog.data.database.Expense
import com.timepassapps.expenselog.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SmsBroadcastReceiver : BroadcastReceiver() {

    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        val smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
        val sender = smsMessage.originatingAddress
        val message = smsMessage.messageBody
        if(isBankSms(sender) && isExpense(message)) {
            val expense = Expense()
            expense.message = message
            expense.bank = getBankName(sender)
            expense.amount = getExpenseAmount(message)
            expense.time = System.currentTimeMillis();
            if(hasBalance(message)) {
                expense.balance = getBalance(message)
            }
            GlobalScope.launch {
                ExpenseDatabase.getInstance(context).expenseDao().insertExpense(expense)
            }
        }
    }
}