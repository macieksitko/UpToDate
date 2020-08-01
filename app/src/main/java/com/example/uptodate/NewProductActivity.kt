package com.example.uptodate

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_new_product.*
import java.util.*

class NewProductActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    var dateOfExpiry = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        setupListeners()

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
            var newMonth = mMonth + 1
            dateOfExpiry = "$mDay/$newMonth/$mYear"
            editTextDateOfExpiry.setText(dateOfExpiry)
        },year,month,day)
        datePicker.show()
    }
    private fun saveProduct(product: Product){
        productViewModel.insert(product)
    }
    private fun setAlarm(dateOfExpiry: String){

    }
}
