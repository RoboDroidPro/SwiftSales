package com.example.salestracker

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.salestracker.data.model.ProductItem

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = ProductItem::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Sale(
    @PrimaryKey val id: String,
    val date: String,
    val productId: String, //Todo foreign key
    val buyer: String,
    val quantity: Int,
    val totalSalePrice: Double,
    val saleNotes: String? = null
)
