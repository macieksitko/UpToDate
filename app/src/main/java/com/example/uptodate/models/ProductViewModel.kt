package com.example.uptodate.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.uptodate.db.ProductDatabase
import com.example.uptodate.db.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository

    val allProducts: LiveData<MutableList<Product>>

    init {
        val productDao = ProductDatabase.getDatabase(
            application
        ).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.allProducts
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(product: Product) = viewModelScope.launch{
        repository.insert(product)
    }
    fun getProduct(id: Long) = viewModelScope.launch(Dispatchers.IO){
        repository.getProduct(id)
    }
    fun deleteProduct(product: Product) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteProduct(product)
    }
    fun insertWithId(product: Product,idAction: (Long,String)->Unit) {
        viewModelScope.launch{
            val id = repository.insertProductWithId(product)
            idAction.invoke(id,product.product_name)
        }
    }
    fun setActive(id: Long,isActive: Boolean)= viewModelScope.launch{
        repository.setActive(id,isActive)
    }

}