package com.example.salestracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
)
data class Sale(  // Represents an event when one or more products were sold
    @PrimaryKey val id: String,
    val date: String,
    val buyer: String,
    val totalSalePrice: Double,
    val saleNotes: String? = null
)
