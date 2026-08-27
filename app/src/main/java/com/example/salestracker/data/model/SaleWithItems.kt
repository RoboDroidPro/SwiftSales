package com.example.salestracker.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.salestracker.Sale

data class SaleWithItems( // Joins a sale with all the items that were sold in it
    @Embedded val sale: Sale,
    @Relation(
        parentColumn = "id",
        entityColumn = "saleId"
    )
    val saleItem: SaleItem
)
