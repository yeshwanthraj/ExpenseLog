package com.timepassapps.expenselog.utils

object AppConstants {
    const val APP_PREFS = "application_prefs"
    const val IS_EXPENSE_DB_GENERATED = "sms_scaned"
}

object SmsConstants {
    const val SMS_CONTENT_URI = "content://sms/inbox"
    const val ID_COLUMN = "_id"
    const val SENDER_COLUMN = "address"
    const val BODY_COLUMN = "body"
    const val TIME_COLUMN = "date_sent"
}

object BankConstants {
   val BANK_NAMES: Map<String, String> = object : HashMap<String, String>() {
        init {
            put("HDFCB", "HDFC Bank")
            put("ICICIB", "ICICI Bank")
            put("CANBNK", "Canara Bank")
            put("ANDBNK", "Andra Bank")
            put("AXISBK", "Axis Bank")
            put("SBI", "SBI Bank")
        }
    }
}

object KeywordConstants {
    const val OTP = "OTP"
    val DEBIT_KEYWORDS = arrayOf("withdraw", "debit", "sent", "transfer")
    val CREDIT_KEYWORDS = arrayOf("credit")
    const val AMOUNT_REGEX = "(([Rr][Ss])|(INR))(\\s*)((\\d{1,2},)*\\d{1,3}.\\d{2})"
    const val CARD_NUMBER_REGEX = "(XX|xx)\\d{4}"
    const val UPI = "UPI"
    const val REQUEST = "request"
}