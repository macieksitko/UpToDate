package com.example.uptodate

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "product_table")
data class Product (
    @PrimaryKey
    val id: Int,
    val product_name: String,
    val date_of_expiry: String //change to DateTimeFormat
)
