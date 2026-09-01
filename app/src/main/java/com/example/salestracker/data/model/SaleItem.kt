package com.example.salestracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.salestracker.SaleEvent

@Entity(tableName = "sale_item",
    foreignKeys = [
        ForeignKey(
            entity = SaleEvent::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        // 2. Link to Product (RESTRICT or NO ACTION)
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT // Prevent deleting a product if it has sales history!
        )
    ],
    indices = [
        Index("saleId"),
        Index("productId")
    ]
)
data class SaleItem(   // Represents one item in a sale (where one sale can have many items)
    @PrimaryKey val id: String,
    val saleId: String,
    val productId: String,
    val salePrice: Double, //represents the price of the sale item. could be quantity 5 were sold, making 5 x Product.price
    val quantity: Int
)