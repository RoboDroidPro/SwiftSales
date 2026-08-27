package com.example.salestracker.ui.screens.sale.add

data class AddSaleUIState1(
    val date: String,
    val buyer: String,
    val totalSalePrice: Double,
    val saleNotes: String? = null,
    val saleItems: List<SaleItemState>
)

data class SaleItemState(
    val saleItemId: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int
)
