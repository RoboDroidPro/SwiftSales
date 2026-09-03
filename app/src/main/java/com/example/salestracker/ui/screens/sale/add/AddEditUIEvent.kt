package com.example.salestracker.ui.screens.sale.add

import androidx.annotation.StringRes

sealed class AddEditUIEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : AddEditUIEvent()
    data class NavigateBack(val resultCode: String) : AddEditUIEvent()
}