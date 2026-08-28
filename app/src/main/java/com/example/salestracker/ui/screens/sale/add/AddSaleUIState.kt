package com.example.salestracker.ui.screens.sale.add

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
    val productName: String = "",
    val productPrice: Double = 0.0,
    val quantity: Int = 1
)
