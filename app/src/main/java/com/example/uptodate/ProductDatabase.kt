package com.example.uptodate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Product::class), version = 1, exportSchema = false)
public abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    private class ProductDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var productDao = database.productDao()

                    // Add sample words.
                    var product = Product (1,product_name = "Baton",date_of_expiry = "2020-10-20")
                    productDao.insertProduct(product)
                    product = Product(2,product_name = "Mleko",date_of_expiry = "2020-10-10")
                    productDao.insertProduct(product)

                }
            }
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ProductDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                )
                    .addCallback(ProductDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
