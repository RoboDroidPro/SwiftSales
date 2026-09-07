package com.example.salestracker.ui.screens.settings

data class SettingsUIState(
    val showDelete: Boolean = false,
    val showDeleteAll: Boolean = false,
    val deleteAllError: Boolean = false,
    val deleteProductError: Int? = null,
    val productName: String = "",
    val productPrice: String = "",
    val productNotes: String = "",
    val productInStock: Boolean = false,
    val productOrderIndex: String = "",
    val productError: Int? = null,
    val priceError: Int? = null,
    val indexError: Int? = null,
)