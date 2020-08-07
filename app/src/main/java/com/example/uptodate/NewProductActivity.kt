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
import kotlinx.android.synthetic.main.product_details_bottom_sheet.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class NewProductActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels()


    private val receiver = DateOfExpiryBroadcastReceiver()
    private var dateOfExpiry = ""
    private var dateOfAdding = ""
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
            val currentDate = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
            dateOfAdding = currentDate.format(formatter)

            val product = Product(productName,dateOfExpiry,dateOfAdding)
            saveProduct(product)
            setAlarm(dateOfExpiry)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun setDatePicker(){
        val c = Calendar.getInstance()
        val datePicker = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
            val newMonth = mMonth + 1
            dateOfExpiry = "$mDay/$newMonth/$mYear"
            editTextDateOfExpiry.setText(dateOfExpiry)
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH))
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

        Log.d("TAG","time left in milliseconds ${millis - time}")
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
