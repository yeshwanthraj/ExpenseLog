package com.timepassapps.expenselog.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
class Expense {

    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

    var bank: String? = null

    var cardNumber: String = ""

    var amount: BigDecimal = BigDecimal(-1)

    var time : Long = 0

    var balance: BigDecimal = BigDecimal(-1)

    var message: String = ""
}