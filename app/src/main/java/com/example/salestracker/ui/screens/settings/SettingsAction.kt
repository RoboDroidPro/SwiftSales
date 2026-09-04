package com.example.salestracker.ui.screens.settings

import com.example.salestracker.data.model.Product

sealed interface SettingsAction {
    data class AddEditProduct(val product: Product?) : SettingsAction
    data class ProductNameChanged(val newName: String) : SettingsAction
    data class ProductPriceChanged(val newPrice: String) : SettingsAction
    data class ProductNotesChanged(val newNotes: String) : SettingsAction
    object ProductInStockToggle : SettingsAction
    data class ProductIndexChanged(val newIndex: String) : SettingsAction
    object SaveProductClicked : SettingsAction
    object DeleteProduct : SettingsAction
    object DeleteAllProducts : SettingsAction
}