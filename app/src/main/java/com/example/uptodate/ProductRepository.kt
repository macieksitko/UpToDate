package com.example.uptodate

import androidx.lifecycle.LiveData

class ProductRepository(private val productDao: ProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allProducts: LiveData<MutableList<Product>> = productDao.getAllProducts()


    suspend fun getProduct(id: Int){
        productDao.getProduct(id)
    }

    suspend fun insert(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }

}