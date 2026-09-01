package com.example.salestracker.ui.screens.sale.list

import com.example.salestracker.data.model.SaleEventWithItems

data class SalesUIState(
    val allSaleEventWithItems: List<SaleEventWithItems> = emptyList(),
    val selectedSaleEventIds: List<String> = emptyList(),
    val isConfirmingDeletion: Boolean = false,
)
