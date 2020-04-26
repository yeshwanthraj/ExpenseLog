package com.timepassapps.expenselog.utils

import androidx.room.TypeConverter
import java.math.BigDecimal

class CurrencyTypeConverter {

    @TypeConverter
    fun getCurrencyString(currency : BigDecimal) : String {
        return currency.toString()
    }

    @TypeConverter
    fun getCurrencyBigDecimal(currencyString : String) : BigDecimal {
        return BigDecimal(currencyString)
    }
}