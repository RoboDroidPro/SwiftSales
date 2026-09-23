package com.example.salestracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.data.model.SaleEventWithItems
import com.example.salestracker.data.repository.SaleRepository
import com.example.salestracker.ui.screens.stats.DateFilterType
import com.example.salestracker.ui.screens.stats.ProductSummary
import com.example.salestracker.ui.screens.stats.StatsAction
import com.example.salestracker.ui.screens.stats.StatsUIState
import com.example.salestracker.utils.ReportExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val saleRepo: SaleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        StatsUIState(
            startDate = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
            endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        observeSales()
    }

    private fun observeSales() {
        viewModelScope.launch {
            saleRepo.allSales.collect { sales ->
                _uiState.update { currentState ->
                    val updated = currentState.copy(allSaleEventWithItems = sales)
                    recalculateStats(updated)
                }
            }
        }
    }

    fun onAction(action: StatsAction) {
        when (action) {
            is StatsAction.SelectFilter -> {
                _uiState.update { currentState ->
                    val updated = currentState.copy(filterType = action.filterType)
                    recalculateStats(updated)
                }
            }
            is StatsAction.UpdateStartDate -> {
                _uiState.update { currentState ->
                    val updated = currentState.copy(startDate = action.date)
                    recalculateStats(updated)
                }
            }
            is StatsAction.UpdateEndDate -> {
                _uiState.update { currentState ->
                    val updated = currentState.copy(endDate = action.date)
                    recalculateStats(updated)
                }
            }
            is StatsAction.UpdateClerkName -> {
                _uiState.update { currentState ->
                    val updated = currentState.copy(clerkName = action.name)
                    recalculateStats(updated)
                }
            }
            StatsAction.ShowExportDialog -> {
                _uiState.update { it.copy(showExportDialog = true) }
            }
            StatsAction.HideExportDialog -> {
                _uiState.update { it.copy(showExportDialog = false) }
            }
        }
    }

    private fun recalculateStats(state: StatsUIState): StatsUIState {
        val filtered = filterSales(
            sales = state.allSaleEventWithItems,
            filterType = state.filterType,
            startDateStr = state.startDate,
            endDateStr = state.endDate
        )

        val totalRevenue = filtered.sumOf { it.saleEvent.totalSalePrice }
        val totalSalesCount = filtered.size
        val totalItemsSold = filtered.sumOf { sale -> sale.items.sumOf { it.saleItem.quantity } }

        val productMap = mutableMapOf<String, Pair<Int, Int>>()
        filtered.forEach { sale ->
            sale.items.forEach { item ->
                val name = item.product.name
                val current = productMap.getOrDefault(name, Pair(0, 0))
                productMap[name] = Pair(
                    current.first + item.saleItem.quantity,
                    current.second + item.saleItem.salePrice
                )
            }
        }

        val productBreakdown = productMap.map { (name, pair) ->
            ProductSummary(
                productName = name,
                totalQuantity = pair.first,
                totalRevenueCents = pair.second
            )
        }.sortedByDescending { it.totalRevenueCents }

        val periodText = getPeriodText(state.filterType, state.startDate, state.endDate)
        val reportText = ReportExporter.generateTextReport(
            clerkName = state.clerkName,
            periodText = periodText,
            sales = filtered
        )

        return state.copy(
            filteredSaleEventWithItems = filtered,
            totalRevenueCents = totalRevenue,
            totalSalesCount = totalSalesCount,
            totalItemsSold = totalItemsSold,
            productBreakdown = productBreakdown,
            reportText = reportText
        )
    }

    private fun filterSales(
        sales: List<SaleEventWithItems>,
        filterType: DateFilterType,
        startDateStr: String,
        endDateStr: String
    ): List<SaleEventWithItems> {
        val today = LocalDate.now()

        return sales.filter { saleWithItems -> //each sale goes through this block individually
            val saleDate = try {  //variable with calendar value of the date, formatted, or null if date is invalid(corrupted)
                LocalDate.parse(saleWithItems.saleEvent.date, DateTimeFormatter.ISO_LOCAL_DATE)
            } catch (e: Exception) {
                null  //if date is corrupted, pass it on as true so the user sees it(signifies something went very wrong)
            } ?: return@filter true

            when (filterType) { //filters out the sale based on the filter type
                DateFilterType.ALL_TIME -> true  //returns true on ALL sales
                DateFilterType.TODAY -> saleDate == today //returns true if the sale date is today
                DateFilterType.THIS_WEEK -> { //returns true if sale date is within this week
                    val monday = today.with(DayOfWeek.MONDAY)
                    val sunday = today.with(DayOfWeek.SUNDAY)
                    !saleDate.isBefore(monday) && !saleDate.isAfter(sunday)
                }
                DateFilterType.THIS_MONTH -> {  //returns true if sale date is within this month
                    val firstDay = today.withDayOfMonth(1)
                    val lastDay = today.withDayOfMonth(today.lengthOfMonth())
                    !saleDate.isBefore(firstDay) && !saleDate.isAfter(lastDay)
                }
                DateFilterType.CUSTOM -> {  //returns true if sale date is within custom dates selected by the user
                    val start = try { LocalDate.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE) } catch (e: Exception) { LocalDate.MIN }
                    val end = try { LocalDate.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE) } catch (e: Exception) { LocalDate.MAX }
                    !saleDate.isBefore(start) && !saleDate.isAfter(end)
                }
            }
        }
    }

    fun getPeriodText(filterType: DateFilterType, startStr: String, endStr: String): String {
        return when (filterType) {
            DateFilterType.ALL_TIME -> "All Time"
            DateFilterType.TODAY -> "Today (${LocalDate.now()})"
            DateFilterType.THIS_WEEK -> "This Week"
            DateFilterType.THIS_MONTH -> "This Month (${LocalDate.now().month.name} ${LocalDate.now().year})"
            DateFilterType.CUSTOM -> "$startStr to $endStr"
        }
    }
}
