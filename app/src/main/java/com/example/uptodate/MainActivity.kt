package com.example.uptodate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.product_details_bottom_sheet.*
import kotlinx.android.synthetic.main.recyclerview_item.*

class MainActivity : AppCompatActivity(),ProductListAdapter.OnProductListener {

    private val productViewModel: ProductViewModel by viewModels()
    private val adapter = ProductListAdapter(this)
    private var onDeletePosition = -1
    private var isProductClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupProductList()
        setupListeners()

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
        val bottomSheetFragment = ProductBottomSheet()
        val product = adapter.getProductAtPosition(position)
        val productName = product.product_name
        val dateOfAdding = product.date_of_adding
        val dateOfExpiring = product.date_of_expiry
        val bundle = Bundle()
        bundle.putString("productName", productName)
        bundle.putString("dateOfAdding", dateOfAdding)
        bundle.putString("dateOfExpiring", dateOfExpiring)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    override fun onProductLongClick(position: Int) {
        adapter.notifyDataSetChanged()
        isProductClicked = true
        onDeletePosition = position
    }

    override fun onImageClick(position: Int) {
        if (isProductClicked){
            deleteSelectedProduct()
            isProductClicked = false
        }
    }
    private fun deleteSelectedProduct(){
        val product= adapter.getProductAtPosition(onDeletePosition)
        productViewModel.deleteProduct(product)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 2){}

    }
}
