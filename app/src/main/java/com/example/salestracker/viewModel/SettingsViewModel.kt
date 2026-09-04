package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.data.model.Product
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.screens.settings.SETTAG
import com.example.salestracker.ui.screens.settings.SettingsUIState
import com.example.salestracker.utils.toSwiftCurrency
import com.example.salestracker.utils.toSwiftString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private var currentProductID: String? = null

    private val _settingsUIState = MutableStateFlow(SettingsUIState())
    val settingsUIState = _settingsUIState.asStateFlow()

    // This replaces your manual _settingsUIState for the list
    val productsList: StateFlow<List<Product>> = repository.productsList
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // keeps flowing while screen subscribed (stops 5s after unsubscribed)
            initialValue = emptyList()                      // shown immediately before DB loads
        )

    private val _showAddProductSheet = MutableStateFlow(false)
    val showAddProductSheet = _showAddProductSheet.asStateFlow()

    //Drawer sheet UI functions

    fun addEditProduct(product: Product? = null) {
        _showAddProductSheet.value = true

        if (product == null) {
            currentProductID = null
            _settingsUIState.update {
                SettingsUIState()
            }
        } else {
            currentProductID = product.id
            _settingsUIState.update {
                SettingsUIState(
                    showDelete = true,
                    productName = product.name,
                    productPrice = product.defaultPrice.toSwiftString(),
                    productNotes = product.notes ?: "",
                    productInStock = product.inStock,
                    productOrderIndex = product.orderIndex.toString()
                )
            }
        }
    }

    fun onSheetDismissed() {
        _showAddProductSheet.value = false
    }

    fun drawerSaveClicked() {
        Log.d(SETTAG, "drawer save clicked")
        var userMessage: String? = null

        if (settingsUIState.value.productName.isBlank()) {
            userMessage = "'Name' field required"
        }

        val price = settingsUIState.value.productPrice
        val priceRegex = Regex("^\\d+(\\.\\d{1,2})?$")
        if (price.isNotBlank()) {
            if (!priceRegex.matches(price)) {
                userMessage =
                    "${userMessage ?: ""} \n'Default Price' invalid. Must be a number with up to 2 decimals. Examples: 2.98, 2.1, 20, 0.2"
            }
        }
        if (settingsUIState.value.productOrderIndex.toIntOrNull() == null) {
            userMessage = "${userMessage ?: ""} \n 'Invalid Order Index'"
        }

        Log.d(SETTAG, "User message is: $userMessage")

        if (userMessage == null) {
            _showAddProductSheet.value = false
            saveProduct(
                Product(
                    id = currentProductID ?: UUID.randomUUID().toString(),
                    name = _settingsUIState.value.productName,
                    defaultPrice = _settingsUIState.value.productPrice.toSwiftCurrency() ?: 0,
                    notes = _settingsUIState.value.productNotes,
                    inStock = _settingsUIState.value.productInStock,
                    orderIndex = _settingsUIState.value.productOrderIndex.toIntOrNull() ?: 0
                )
            )
        } else {
            _settingsUIState.update { it.copy(userMessage = userMessage) }
        }
    }

    //UI STATE functions
    fun productNameChanged(newValue: String) {
        _settingsUIState.update {
            it.copy(
                productName = newValue,
                userMessage = null
            )
        }
    }

    fun productPriceChanged(newValue: String) {
        _settingsUIState.update {
            it.copy(
                productPrice = newValue,
                userMessage = null
            )
        }
    }

    fun productNotesChanged(newValue: String) {
        _settingsUIState.update {
            it.copy(
                productNotes = newValue,
                userMessage = null
            )
        }
    }

    fun inStockToggle() {
        _settingsUIState.update { state ->
            state.copy(
                productInStock = !state.productInStock
            )
        }
    }

    fun productIndexChanged(newValue: String) {
        _settingsUIState.update {
            it.copy(
                productOrderIndex = newValue,
                userMessage = null
            )
        }
    }

    //Database actions
    fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteProductItems()
            } catch (e: Exception) {
                Log.e(SETTAG, "Error deleting all products: ${e.message}")
                _settingsUIState.update {
                    it.copy(userMessage = "Cannot delete all products: Some are used in existing sales. Delete all sales first.")
                }
            }
        }
    }

    private fun saveProduct(product: Product) {
        Log.d(SETTAG, "save product called with product ${product.name}")
        viewModelScope.launch {
            repository.upsertProductItem(product)
        }
    }

    fun deleteProduct() {
        viewModelScope.launch {
            try {
                repository.deleteProduct(
                    productId = currentProductID!!
                )
                _showAddProductSheet.value = false
            } catch (e: Exception) {
                Log.e(SETTAG, "Error deleting product: ${e.message}")
                _settingsUIState.update {
                    it.copy(userMessage = "Cannot delete: This product is used in existing sales. Delete the sales first.")
                }
            }
        }
    }
}