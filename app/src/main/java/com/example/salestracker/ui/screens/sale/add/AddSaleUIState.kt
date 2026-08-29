package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.data.model.Product
import java.util.UUID

data class AddSaleUIState(
    val date: String = "8/28/26",
    val buyer: String = "",
    val totalSalePrice: Double = 0.00,
    val saleNotes: String = "",
    val saleItems: List<SaleItemState> = listOf(SaleItemState()),

    val buyerError: String? = null,
    val totalSalePriceError: String? = null,
    val itemsError: String? = null
)

data class SaleItemState(
    val saleItemId: String = UUID.randomUUID().toString(),
    val product: Product = Product(),
    val salePrice: Double = 0.00,
    val quantity: Int? = 1
)
