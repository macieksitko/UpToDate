package com.example.uptodate

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
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

        //productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        productViewModel.allProducts.observe(this, Observer {products->
            products?.let { adapter.setProducts(it) }
        })
    }
    private fun setupListeners(){

        addButton.setOnClickListener{
            val intent = Intent(this, NewProductActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onProductClick(position: Int) {
        TODO()
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
        val product= adapter.getWordAtPosition(onDeletePosition)
        productViewModel.deleteProduct(product)
    }
}
