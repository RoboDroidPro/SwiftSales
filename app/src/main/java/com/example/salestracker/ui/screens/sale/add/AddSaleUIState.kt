package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.data.model.Product
import java.util.UUID

data class AddSaleUIState(
    val screenTitle: String = "Add Sale",
    val date: String = "",
    val buyer: String = "",
    val totalSalePrice: String = "",
    val saleNotes: String = "",
    val saleItems: List<SaleItemState> = listOf(SaleItemState()),
    val showDialog: Boolean = false,

    val buyerError: String? = null,
    val totalSalePriceError: String? = null,
    val itemsError: String? = null
)

data class SaleItemState(
    val saleItemId: String = UUID.randomUUID().toString(),
    val product: Product = Product(),
    val lineTotal: String = "",
    val unitPrice: String = "",
    val quantity: Int? = null,
    val productError: String? = null,
    val unitPriceError: String? = null,
)
