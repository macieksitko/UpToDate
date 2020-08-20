package com.example.uptodate.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.uptodate.models.Product


@Dao
interface ProductDao {

    @Query("SELECT id, product_name,date_of_expiry,date_of_adding,isActive FROM product_table")
    fun getAllProducts(): LiveData<MutableList<Product>>

    @Query("SELECT * FROM product_table as p WHERE p.id = :id")
    suspend fun getProduct(id: Long): Product

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProductWithId(product: Product):Long

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT id FROM product_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastId():Long

    @Query("UPDATE product_table SET isActive = :isActive WHERE id = :id")
    suspend fun setActive(id: Long,isActive: Boolean)

}