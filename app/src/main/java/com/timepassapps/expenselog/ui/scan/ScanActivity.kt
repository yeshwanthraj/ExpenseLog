package com.timepassapps.expenselog.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.timepassapps.expenselog.R
import com.timepassapps.expenselog.utils.*
import kotlinx.android.synthetic.main.activity_scan.*
import androidx.lifecycle.ViewModelProvider
import com.timepassapps.expenselog.data.SharedPrefs
import com.timepassapps.expenselog.ui.list.ExpenseListActivity


class ScanActivity : AppCompatActivity() {

	private val TAG = javaClass.simpleName

	private val SMS_PERMISSION_REQUEST = 100
	private lateinit var viewModel : ScanViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_scan)
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
		viewModel.generateExpenseDb()
		viewModel.isExpenseDbGenerated.observe(this, Observer {
			SharedPrefs.getInstance(this).putBoolean(AppConstants.IS_EXPENSE_DB_GENERATED,true)
			val intent = Intent(this, ExpenseListActivity::class.java)
			startActivity(intent)
		})
	}

	private fun hasPermission() : Boolean =
			ActivityCompat
					.checkSelfPermission(this,Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
}
