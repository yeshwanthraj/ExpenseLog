package com.timepassapps.expenselog.utils

import androidx.annotation.Nullable
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

fun getBankName(sender: String): String {
    for (bank in BankConstants.BANK_NAMES.keys) {
        if (sender.contains(bank)) {
            return BankConstants.BANK_NAMES[bank].toString()
        }
    }
    return ""
}

fun isDebit(message: String): Boolean  {
        if(message.contains(KeywordConstants.OTP)) return false
        if(message.contains(KeywordConstants.REQUEST)) return false
        if(containsDebitKeywords(message) && hasExpense(message)) return true
    return false
}

fun isCredit(message: String) : Boolean =
        if(message.contains(KeywordConstants.OTP)) {
            false
        } else {
            containsCreditKeywords(message)
        }

private fun containsDebitKeywords(message: String): Boolean {
    val msg = message.toLowerCase()
    for (keyword in KeywordConstants.DEBIT_KEYWORDS) {
        if (msg.contains(keyword)) {
            return true
        }
    }
    return false
}

private fun containsCreditKeywords(message: String) : Boolean {
    val msg = message.toLowerCase()
    for(keyword in KeywordConstants.CREDIT_KEYWORDS) {
        if(msg.contains(keyword)) {
            return true
        }
    }
    return false
}

fun getBalance(message: String): BigDecimal =
    if (hasBalance(message)) {
        val pattern = Pattern.compile(KeywordConstants.AMOUNT_REGEX)
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
    if (isDebit(message)) {
        val pattern = Pattern.compile(KeywordConstants.AMOUNT_REGEX)
        val matcher = pattern.matcher(message)
        if (matcher.find() && matcher.groupCount() >= 5) {
            BigDecimal(matcher.group(5).replace(",",""))
        } else {
            BigDecimal(-1)
        }
    } else {
        BigDecimal(-1)
    }

fun hasExpense(message : String) : Boolean {
    val p = Pattern.compile(KeywordConstants.AMOUNT_REGEX)
    val m = p.matcher(message)
    return m.find()
}

fun isUpiExpense(message: String): Boolean  =
    message.contains(KeywordConstants.UPI,ignoreCase = false)

fun hasBalance(message: String): Boolean {
    val p = Pattern.compile(KeywordConstants.AMOUNT_REGEX)
    val m = p.matcher(message)
    var count = 0
    while (m.find()) {
        count += 1
    }
    return count == 2
}

@Nullable
fun getCardNumber(message: String): String? {
    val pattern = Pattern.compile(KeywordConstants.CARD_NUMBER_REGEX)
    val matcher = pattern.matcher(message)
    return if (matcher.find()) {
        matcher.group()
    } else {
        null
    }
}
