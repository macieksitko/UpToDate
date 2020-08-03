package com.example.uptodate

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_product.*
import java.text.SimpleDateFormat
import java.util.*


class NewProductActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private var dateOfExpiry = ""
    private val receiver = DateOfExpiryBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)
        setupListeners()
        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun setupListeners(){
        calendarIcon.setOnClickListener{
            setDatePicker()
        }
        submitBtn.setOnClickListener{
            val productName = textInputProductName.editText?.text.toString().trim()
            //val dateOfExpiry = textInputDateOfExpiry.editText?.text.toString().trim()

            val product = Product(productName,dateOfExpiry)
            saveProduct(product)
            setAlarm(dateOfExpiry)

        }
    }

    private fun setDatePicker(){
        val datePicker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
            val newMonth = mMonth + 1
            dateOfExpiry = "$mDay/$newMonth/$mYear"
            editTextDateOfExpiry.setText(dateOfExpiry)
        },year,month,day)
        datePicker.show()
    }
    private fun saveProduct(product: Product){
        productViewModel.insert(product)
    }

    private fun setAlarm(dateOfExpiry: String){
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = sdf.parse(dateOfExpiry)
        val millis = date.time
        val time = System.currentTimeMillis()

        Log.d("TAG","time to left in milliseconds ${millis - time}")
        val alarms =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val filter = IntentFilter("ALARM_ACTION")
        registerReceiver(receiver, filter)

        val notificationIntent = Intent(this, DateOfExpiryBroadcastReceiver::class.java)

        val operation: PendingIntent =
            PendingIntent.getBroadcast(this, 0, notificationIntent, 0)
        alarms.setExact(AlarmManager.RTC_WAKEUP, millis, operation)
    }
    private fun createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
