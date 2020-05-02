package com.timepassapps.expenselog.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.timepassapps.expenselog.R
import com.timepassapps.expenselog.utils.*
import com.timepassapps.expenselog.viewmodels.ScanViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.timepassapps.expenselog.data.SharedPrefs


class ScanActivity : AppCompatActivity(), CoroutineScope {

    private val TAG = javaClass.simpleName

    val job = Job()
    override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main
    private val SMS_PERMISSION_REQUEST = 100
    private lateinit var viewModel : ScanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)
        if(!hasPermission()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                smsPermissionWarning.show()
            } else {
                grantPermission.show()
                grantPermission.setOnClickListener {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS),SMS_PERMISSION_REQUEST)
                }
            }
        } else {
            if(!SharedPrefs.getInstance(this).getBoolean(AppConstants.IS_EXPENSE_DB_GENERATED,false)) {
                generateExpenseDb()
                Toast.makeText(this,"Expenses generated",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            SMS_PERMISSION_REQUEST ->
                if(permissions[0] == Manifest.permission.READ_SMS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!SharedPrefs.getInstance(this).getBoolean(AppConstants.IS_EXPENSE_DB_GENERATED, false)) {
                        generateExpenseDb()
                        Toast.makeText(this,"Expenses generated",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun generateExpenseDb() {
        launch {
            val expenseList = viewModel.generateExpenses()
            expenseList?.let {
                viewModel.insertExpenses(expenseList)
            }
        }
        SharedPrefs.getInstance(this).putBoolean(AppConstants.IS_EXPENSE_DB_GENERATED,true)
    }

//    private fun getCurrentMonthExpenseList() {
//        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
//        val year = Calendar.getInstance().get(Calendar.YEAR)
//        val firstDayOfMonth = "01/$month/$year"
//        val dateFormat = SimpleDateFormat("dd/mm/yy")
//        val time = dateFormat.parse(firstDayOfMonth).time
//        expenseList = expenseLogApplication.getDatabase().expenseDao().getExpensesAfter(time)
//        expenseList.sortedByDescending { it.time }
//        for(i in expenseList.size..0) {
//            if(expenseList[i].balance.intValueExact() == -1) {
//                expenseList[i].balance = expenseList[i-1].balance - expenseList[i].amount
//            }
//        }
////        Toast.makeText(this, "list size is ${mExpenseList.size}", Toast.LENGTH_SHORT).show()
//        expenseLogApplication.appExecutors.mainThread().execute {
//            Toast.makeText(this, "Currency balance = ${expenseList[1].balance}", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun hasPermission() : Boolean =
            ActivityCompat
                    .checkSelfPermission(this,Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
}
