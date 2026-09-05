package com.example.salestracker.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.salestracker.Sale

data class ProductSale(
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductItem
)
