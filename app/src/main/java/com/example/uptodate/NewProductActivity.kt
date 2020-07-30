package com.example.uptodate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_new_product.*

class NewProductActivity : AppCompatActivity() {
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        setupListeners()
    }

    private fun setupListeners(){
        submitBtn.setOnClickListener{
            val productName = textInputProductName.editText?.text.toString().trim()
            val dateOfExpiry = textInputDateOfExpiry.editText?.text.toString().trim()

            val product = Product(productName,dateOfExpiry)
            saveProduct(product)
        }
    }
    private fun saveProduct(product: Product){
        productViewModel.insert(product)
    }
}
