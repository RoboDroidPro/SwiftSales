package com.example.salestracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductItem(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val defaultPrice: Double = 0.0,
    val inStock: Boolean = false, //just added this
    val notes: String? = null,
    val orderIndex: Int = 0
)
