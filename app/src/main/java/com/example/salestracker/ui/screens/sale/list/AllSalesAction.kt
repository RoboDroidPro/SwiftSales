package com.example.salestracker.ui.screens.sale.list

sealed interface AllSalesAction {
    object ClearSelection : AllSalesAction
    data class ToggleSelection(val saleId: String) : AllSalesAction
    object SelectAllSales : AllSalesAction
    object AskForDeletionConfirmation : AllSalesAction
    object DeleteSelectedSales : AllSalesAction
}