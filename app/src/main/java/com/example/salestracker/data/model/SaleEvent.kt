package com.example.salestracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
)
data class SaleEvent(  // Represents an event when one or more products were sold
    @PrimaryKey val id: String,
    val date: String,
    val buyer: String,
    val totalSalePrice: Int,
    val saleNotes: String? = null
)