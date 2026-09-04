package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.data.model.Product
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.screens.settings.SETTAG
import com.example.salestracker.ui.screens.settings.SettingsAction
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

    fun onAction(action: SettingsAction) {
        _settingsUIState.update { it.copy(deleteProductError = null, deleteAllError = false) }
        when (action) {
            is SettingsAction.AddEditProduct -> addEditProduct(action.product)
            SettingsAction.DeleteAllProducts -> deleteAll()
            SettingsAction.DeleteProduct -> deleteProduct()
            SettingsAction.ProductInStockToggle -> {
                _settingsUIState.update { state ->
                    state.copy(
                        productInStock = !state.productInStock
                    )
                }
            }
            is SettingsAction.ProductIndexChanged -> {
                _settingsUIState.update {
                    it.copy(
                        productOrderIndex = action.newIndex,
                        indexError = null
                    )
                }
            }
            is SettingsAction.ProductNameChanged -> {
                _settingsUIState.update {
                    it.copy(
                        productName = action.newName,
                        productError = null
                    )
                }
            }
            is SettingsAction.ProductNotesChanged -> {
                _settingsUIState.update {
                    it.copy(
                        productNotes = action.newNotes,
                    )
                }
            }
            is SettingsAction.ProductPriceChanged -> {
                _settingsUIState.update {
                    it.copy(
                        productPrice = action.newPrice,
                        priceError = if (action.newPrice.toSwiftCurrency() == null) "Invalid price" else null
                    )
                }
            }
            SettingsAction.SaveProductClicked -> drawerSaveClicked()
            SettingsAction.DeleteAllCancel -> _settingsUIState.update { it.copy(showDeleteAll = false) }
            SettingsAction.DeleteAllClicked -> _settingsUIState.update { it.copy(showDeleteAll = true) }
        }
    }
    private fun addEditProduct(product: Product? = null) {
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

        val priceInt = _settingsUIState.value.productPrice.toSwiftCurrency()
        val productError = if (_settingsUIState.value.productName.isBlank()) "Product name required" else null
        val priceError = if (_settingsUIState.value.productPrice.isBlank()) "Price required"
        else if (priceInt == null) "Invalid price"
        else null
        val indexError = if (settingsUIState.value.productOrderIndex.toIntOrNull() == null) "Invalid Order Index" else null

        if (productError == null &&
            priceError == null &&
            indexError == null) {
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
            _settingsUIState.update {
                it.copy(
                    productError = productError,
                    priceError = priceError,
                    indexError = indexError
                )
            }
        }
    }

    //Database actions
    private fun deleteAll() {
        viewModelScope.launch {
            _settingsUIState.update { it.copy(showDeleteAll = false) }
            try {
                repository.deleteProductItems()
            } catch (e: Exception) {
                Log.e(SETTAG, "Error deleting all products: ${e.message}")
                _settingsUIState.update {
                    it.copy(deleteAllError = true)
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

    private fun deleteProduct() {
        viewModelScope.launch {
            try {
                repository.deleteProduct(
                    productId = currentProductID!!
                )
                _showAddProductSheet.value = false
            } catch (e: Exception) {
                Log.e(SETTAG, "Error deleting product: ${e.message}")
                _settingsUIState.update {
                    it.copy(deleteProductError = "Cannot delete: This product is used in existing sales. Delete the sales first.")
                }
            }
        }
    }
}