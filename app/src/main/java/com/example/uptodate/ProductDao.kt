package com.example.uptodate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    @Query("SELECT id, product_name,date_of_expiry FROM product_table")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM product_table as p WHERE p.id = :id")
    suspend fun getProduct(id: Int): Product

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

}