package com.example.salestracker.ui.screens.stats

import com.example.salestracker.data.model.SaleEventWithItems

enum class DateFilterType {
    ALL_TIME,
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    CUSTOM
}

data class ProductSummary(
    val productName: String,
    val totalQuantity: Int,
    val totalRevenueCents: Int
)

data class StatsUIState(
    val filterType: DateFilterType = DateFilterType.THIS_MONTH,
    val startDate: String = "",
    val endDate: String = "",
    val clerkName: String = "",
    val allSaleEventWithItems: List<SaleEventWithItems> = emptyList(), //Todo this might be unnecessary. Can put it in viewModel as a private val
    val filteredSaleEventWithItems: List<SaleEventWithItems> = emptyList(),
    val totalRevenueCents: Int = 0,
    val totalSalesCount: Int = 0,
    val totalItemsSold: Int = 0,
    val productBreakdown: List<ProductSummary> = emptyList(),
    val reportText: String = "",
    val showExportDialog: Boolean = false
)
