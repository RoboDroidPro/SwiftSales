package com.example.salestracker.ui.screens.settings

data class SettingsUIState(
    val showDelete: Boolean = false,
    val userMessage: String? = null,
    val productName: String = "",
    val productPrice: String = "",
    val productNotes: String = "",
    val productInStock: Boolean = false,
    val productOrderIndex: String = ""
)