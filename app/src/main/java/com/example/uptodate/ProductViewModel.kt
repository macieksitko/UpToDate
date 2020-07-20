package com.example.uptodate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository

    val allProducts: LiveData<List<Product>>

    init {
        val productDao = ProductDatabase.getDatabase(application,viewModelScope).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.allProducts
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(product)
    }
    fun getProduct(id: Int) = viewModelScope.launch(Dispatchers.IO){
        repository.getProduct(id)
    }

    fun deleteProduct(product: Product) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteProduct(product)
    }
}