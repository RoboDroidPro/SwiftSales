package com.example.salestracker.viewModel

import androidx.annotation.StringRes

sealed class UIEvent {
    data class ShowSnackbar(@StringRes val messageRes: Int) : UIEvent()
    data class NavigateBack(val resultCode: String) : UIEvent()
}

//data class NavigateBack(val resultCode: Int) : UIEvent()
//