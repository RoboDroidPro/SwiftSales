package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.data.model.Product

data class AddSaleUIState(
    val date: String = "",
    val buyer: String = "",
    val totalSalePrice: String = "",
    val saleNotes: String = "",
    val saleItems: List<SaleItemState> = emptyList(),
    val userMessage: String? = null
)

data class SaleItemState(
    val saleItemId: String = "",
    val product: Product = Product(),
    val quantity: Int = 1
)
