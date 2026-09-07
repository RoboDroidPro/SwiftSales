package com.example.salestracker.ui.screens.sale.add

import com.example.salestracker.R
import com.example.salestracker.data.model.Product
import java.util.UUID

data class AddSaleUIState(
    val screenTitle: Int = R.string.add_sale_title,
    val date: String = "",
    val buyer: String = "",
    val totalSalePrice: String = "",
    val saleNotes: String = "",
    val saleItems: List<SaleItemState> = listOf(SaleItemState()),
    val showDialog: Boolean = false,

    val buyerError: Int? = null,
    val totalSalePriceError: Int? = null,
    val itemsError: Int? = null
)

data class SaleItemState(
    val saleItemId: String = UUID.randomUUID().toString(),
    val product: Product = Product(),
    val lineTotal: String = "",
    val unitPrice: String = "",
    val quantity: Int? = null,
    val productError: Int? = null,
    val unitPriceError: Int? = null,
)
