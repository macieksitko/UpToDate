package com.example.uptodate.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.example.uptodate.receivers.DateOfExpiryBroadcastReceiver
import com.example.uptodate.R
import com.example.uptodate.models.Product
import com.example.uptodate.models.ProductViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_product.*
import kotlinx.android.synthetic.main.dialog_ondelete.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val MILLIS_IN_DAY = 86400000
class NewProductActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels()
    private val receiverExactDate =
        DateOfExpiryBroadcastReceiver()
    private var dateOfExpiry = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)
        registerReceivers()
        setupListeners()
        setProgressBarGone()
    }
    private fun registerReceivers(){
        val filter = IntentFilter("ALARM_ACTION")
        registerReceiver(receiverExactDate, filter)
    }
    private fun setProgressBarGone() {
        newProductProgressBar.visibility = View.GONE
    }
    private fun setProgressBarVisible() {
        newProductProgressBar.visibility = View.VISIBLE
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceivers()
    }
    private fun unregisterReceivers(){
        unregisterReceiver(receiverExactDate)
    }
    private fun setupListeners(){
        backArrow.setOnClickListener {
            showCustomDialog()
        }
        textInputProductName.setOnClickListener{
            textInputProductName.error = null
        }
        textInputDateOfExpiry.setOnClickListener{
            textInputDateOfExpiry.error = null
        }
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
        productViewModel.insertWithId(product) {id,prodName->setAlarmForProductId(id,prodName)}
    }

    private fun setAlarmForProductId(id: Long,prodName:String) {
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

        val dayBeforeNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java).apply {
            action = "com.example.uptodate.activities.NewProductActivity"
            putExtra("submittedProductName", prodName)
            putExtra("notificationID", 0)
            putExtra("id", 1) }

        val exactDayNotificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java).apply {
            action = "com.example.uptodate.activities.NewProductActivity"
            putExtra("submittedProductId", prodId)
            putExtra("submittedProductName", prodName)
            putExtra("id", 2)
        }

        val dayBeforeId = (prodId.toString()+0).toInt()
        val exactDayId = (prodId.toString()+1).toInt()

        val dayBeforeWarning: PendingIntent =
            PendingIntent.getBroadcast(this, dayBeforeId, dayBeforeNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exactDayWarning: PendingIntent =
            PendingIntent.getBroadcast(this, exactDayId, exactDayNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarms.setExact(AlarmManager.RTC_WAKEUP,dayBeforeDateInMillis,dayBeforeWarning)
        alarms.setExact(AlarmManager.RTC_WAKEUP, dateInMillis, exactDayWarning)
    }
    private fun showCustomDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_oncancel, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val mAlertCreate = mBuilder.create()
        mAlertCreate.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val mAlertDialog = mBuilder.show()

        dialogView.btnNegative.setOnClickListener{
            mAlertDialog.dismiss()
        }
        dialogView.btnPositive.setOnClickListener{
            finish()
            mAlertDialog.dismiss()
        }

    }

}
