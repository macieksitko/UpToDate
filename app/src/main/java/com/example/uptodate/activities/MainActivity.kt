package com.example.uptodate.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uptodate.fragments.ProductBottomSheet
import com.example.uptodate.adapters.ProductListAdapter
import com.example.uptodate.R
import com.example.uptodate.adapters.selectedPosition
import com.example.uptodate.models.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_ondelete.view.*

class MainActivity : AppCompatActivity(),
    ProductListAdapter.OnProductListener
{

    private val productViewModel: ProductViewModel by viewModels()
    private val adapter = ProductListAdapter(this)
    private var onDeletePosition = -1
    private var isProductClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupProductList()
        setupListeners()
        createNotificationChannel()

    }
    private fun setupProductList(){

        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        productViewModel.allProducts.observe(this, Observer {products->
            products?.let { adapter.setProducts(it) }
        })
    }
    private fun setupListeners(){
        addButton.setOnClickListener{
            val intent = Intent(this, NewProductActivity::class.java)
            startActivityForResult(intent,2)
        }
    }
    override fun onProductClick(position: Int) {
        if (isProductClicked){
            selectedPosition = -1
            isProductClicked = false
            adapter.notifyDataSetChanged()
        }else{
            val bottomSheetFragment =
                ProductBottomSheet()
            val product = adapter.getProductAtPosition(position)
            val productName = product.product_name
            val dateOfAdding = product.date_of_adding
            val dateOfExpiring = product.date_of_expiry
            val isActive = product.isActive
            val bundle = Bundle()
            Log.d("TAG","product id ${product.id}")
            bundle.putString("productName", productName)
            bundle.putString("dateOfAdding", dateOfAdding)
            bundle.putString("dateOfExpiring", dateOfExpiring)
            bundle.putBoolean("activityState", isActive)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    override fun onProductLongClick(position: Int) {
        isProductClicked = true
        onDeletePosition = position
        adapter.notifyDataSetChanged()
    }

    override fun onImageClick(position: Int) {
        if (isProductClicked){
            showCustomDialog()
        }
    }
    private fun deleteSelectedProduct(){

        val product= adapter.getProductAtPosition(onDeletePosition)
        productViewModel.deleteProduct(product)
        isProductClicked = false
    }
    private fun showCustomDialog(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ondelete,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val mAlertCreate = mBuilder.create()
        mAlertCreate.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val mAlertDialog = mBuilder.show()

        dialogView.btnNegative.setOnClickListener{
            mAlertDialog.dismiss()
        }
        dialogView.btnPositive.setOnClickListener{
            deleteSelectedProduct()
            mAlertDialog.dismiss()
        }

    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 2){}

    }*/
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
