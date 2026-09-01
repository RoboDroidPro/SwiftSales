package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.data.model.Product
import java.util.UUID

data class AddSaleUIState(
    val date: String = "",
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
    val lineTotal: Double = 0.0,
    val unitPrice: Double = 0.0,
    val quantity: Int? = null,
    val productError: String? = null,
    val unitPriceError: String? = null,
)
