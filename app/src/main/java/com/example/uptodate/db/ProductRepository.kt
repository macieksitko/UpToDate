package com.example.uptodate.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.uptodate.models.Product


class ProductRepository(private val productDao: ProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allProducts: LiveData<MutableList<Product>> = productDao.getAllProducts()

    suspend fun getProduct(id: Long){
        productDao.getProduct(id)
    }

    suspend fun insert(product: Product){
        productDao.insertProduct(product)
    }
    suspend fun insertProductWithId(product: Product):Long{
        return productDao.insertProductWithId(product)
    }

    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }

    suspend fun setActive(id:Long,isActive: Boolean){
        productDao.setActive(id,isActive)
    }
}