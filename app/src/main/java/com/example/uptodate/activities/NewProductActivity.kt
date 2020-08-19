package com.example.uptodate.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.example.uptodate.receivers.DateOfExpiryBroadcastReceiver
import com.example.uptodate.R
import com.example.uptodate.models.Product
import com.example.uptodate.models.ProductViewModel
import kotlinx.android.synthetic.main.activity_new_product.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NewProductActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val receiverExactDate =
        DateOfExpiryBroadcastReceiver()
    private var dateOfExpiry = ""
    private val filter = IntentFilter("ALARM_ACTION")
    private val MILLIS_IN_DAY = 86400000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)
        registerReceiver(receiverExactDate, filter)
        setupListeners()
        setProgressBarGone()
    }

    private fun setProgressBarGone() {
        newProductProgressBar.visibility = View.GONE
    }
    private fun setProgressBarVisible() {
        newProductProgressBar.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiverExactDate)
    }

    private fun setupListeners(){
        calendarIcon.setOnClickListener{
            dateOfExpiry = setDatePicker()
        }
        submitBtn.setOnClickListener{
            val productName = textInputProductName.editText?.text.toString().trim()
            val product = Product(
                productName,
                dateOfExpiry,
                getCurrentDate(),
                isActive = true
            )
           if(isNewProductCorrect()){
               saveProduct(product)
               setProgressBarVisible()
               setResult(Activity.RESULT_OK)
               finish()
           }else setResult(Activity.RESULT_CANCELED)
        }
    }

    private fun isNewProductCorrect():Boolean {
        return if (textInputProductName.editText?.text.toString().trim().isEmpty() || (textInputDateOfExpiry.editText?.text.toString().trim().isEmpty())){
            if(textInputProductName.editText?.text.toString().trim().isEmpty()) textInputProductName.error = getString(R.string.errorNewProductName)
            if (textInputDateOfExpiry.editText?.text.toString().trim().isEmpty())  textInputDateOfExpiry.error = getString(R.string.errorNewProductDate)
            false
        } else true
    }

    private fun setDatePicker():String{
        val c = Calendar.getInstance()
        val datePicker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
            val newMonth = mMonth + 1
            dateOfExpiry = "$mDay/$newMonth/$mYear"
            editTextDateOfExpiry.setText(dateOfExpiry)
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
        return dateOfExpiry
    }

    private fun saveProduct(product: Product){
        //high order functions
        productViewModel.insertWithId(product) {id,prodName->setAlarmForProductId(id,prodName)}
    }

    private fun setAlarmForProductId(id: Long,prodName:String) {
        Log.d("TAG","dodano produkt o id.... $id")
        val dateInMillis = parseDateToMillis(dateOfExpiry)
        setAlarm(dateInMillis,id,prodName)
    }
    private fun getCurrentDate():String{
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(formatter)
    }

    private fun parseDateToMillis(dateOfExpiry: String):Long{
        val dateFilter = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = dateFilter.parse(dateOfExpiry)
        return date.time
    }
    private fun setAlarm(dateInMillis: Long,prodId: Long,prodName: String){
        val dayBeforeDateInMillis = dateInMillis - MILLIS_IN_DAY

        val alarms =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dayBeforeNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java)
            dayBeforeNotificationIntent.putExtra("submittedProductName", prodName)
            dayBeforeNotificationIntent.putExtra("notificationID", 0)
            dayBeforeNotificationIntent.putExtra("id", 1)


        val exactDayNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java)
            exactDayNotificationIntent.putExtra("submittedProductId", prodId)
            exactDayNotificationIntent.putExtra("submittedProductName", prodName)
            exactDayNotificationIntent.putExtra("id", 2)


        val dayBeforeWarning: PendingIntent =
            PendingIntent.getBroadcast(this, 0, dayBeforeNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exactDayWarning: PendingIntent =
            PendingIntent.getBroadcast(this, 1, exactDayNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarms.setExact(AlarmManager.RTC_WAKEUP,dayBeforeDateInMillis,dayBeforeWarning)
        alarms.setExact(AlarmManager.RTC_WAKEUP, dateInMillis, exactDayWarning)
    }

}
