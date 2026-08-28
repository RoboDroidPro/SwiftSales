package com.example.salestracker.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.salestracker.SaleEvent

data class SaleEventWithItems( // Joins a sale with all the items that were sold in it
    @Embedded val saleEvent: SaleEvent,
    @Relation(
        entity = SaleItem::class,
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val items: List<SaleItemWithProduct> // The nested list
)

data class SaleItemWithProduct(
    @Embedded val saleItem: SaleItem,
    @Relation(
        parentColumn = "productId", // The link in SaleItem
        entityColumn = "id"         // The link in Product
    )
    val product: Product
)
