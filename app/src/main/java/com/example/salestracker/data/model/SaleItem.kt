package com.example.salestracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.salestracker.Sale

@Entity(tableName = "sale_item",
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class SaleItem(   // Represents one item in a sale (where one sale can have many items)
    @PrimaryKey val id: String,
    val saleId: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int
)