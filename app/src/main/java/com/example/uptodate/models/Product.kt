package com.example.uptodate.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "product_table")
data class Product (
    val product_name: String,
    val date_of_expiry: String,
    val date_of_adding: String,
    val isActive: Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
