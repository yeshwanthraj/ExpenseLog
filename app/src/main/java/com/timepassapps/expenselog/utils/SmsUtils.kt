package com.timepassapps.expenselog.utils

import android.util.Log
import androidx.annotation.Nullable
import com.timepassapps.expenselog.utils.BankConstants
import java.math.BigDecimal
import java.util.regex.Pattern


fun isBankSms(sender: String): Boolean {
    for (bank in BankConstants.BANK_NAMES.keys) {
        if (sender.contains(bank)) {
            return true
        }
    }
    return false
}

@Nullable
fun getBankName(sender: String): String? {
    for (bank in BankConstants.BANK_NAMES.keys) {
        if (sender.contains(bank)) {
            return BankConstants.BANK_NAMES[bank]
        }
    }
    return null
}

fun isExpense(message: String): Boolean  =
    if (message.contains(BankConstants.OTP)) {
        false
    } else {
        containsExpenseKeywords(message)
    }

private fun containsExpenseKeywords(message: String): Boolean {
    var msg = message.toLowerCase()
    for (i in BankConstants.EXPENSE_KEYWORDS.indices) {
        if (msg.contains(BankConstants.EXPENSE_KEYWORDS[i])) {
            return true
        }
    }
    return false
}

fun getBalance(message: String): BigDecimal =
    if (hasBalance(message)) {
        val pattern = Pattern.compile(BankConstants.AMOUNT_REGEX)
        val matcher = pattern.matcher(message)
        // Find the first match and ignore its results
        matcher.find()
        // Now find the next match for patter in the string
        if (matcher.find() && matcher.groupCount() >= 5) {
            BigDecimal(matcher.group(5).replace(",",""))
//            Log.d("TAG", "getBalance: balance is $balance")
        } else {
            BigDecimal(-1)
        }
    } else {
         BigDecimal(-1)
    }

fun getExpenseAmount(message: String): BigDecimal =
    if (isExpense(message)) {
        val pattern = Pattern.compile(BankConstants.AMOUNT_REGEX)
        val matcher = pattern.matcher(message)
        if (matcher.find() && matcher.groupCount() >= 5) {
            BigDecimal(matcher.group(5).replace(",",""))
        } else {
            BigDecimal(-1)
        }
    } else {
        BigDecimal(-1)
    }

fun hasBalance(message: String): Boolean {
    val p = Pattern.compile(BankConstants.AMOUNT_REGEX)
    val m = p.matcher(message)
    var count = 0
    while (m.find()) {
        count += 1
    }
    return count == 2
}

@Nullable
fun getCardNumber(message: String): String? {
    val pattern = Pattern.compile(BankConstants.CARD_NUMBER_REGEX)
    val matcher = pattern.matcher(message)
    return if (matcher.find()) {
        matcher.group()
    } else {
        null
    }
}
