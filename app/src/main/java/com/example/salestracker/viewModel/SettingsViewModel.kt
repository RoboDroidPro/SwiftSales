package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.data.model.Product
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.screens.settings.SETTAG
import com.example.salestracker.utils.toSwiftCurrency
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

data class SettingsUIState(
    val showDelete: Boolean = false,
    val userMessage: String? = null,
    val productName: String = "",
    val productPrice: String = "",
    val productNotes: String = "",
    val productInStock: Boolean = false,
    val productOrderIndex: String = ""
)

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

    init {
//Todo add first time example products

// Optional: pre-fill defaults only on first ever launch
//        viewModelScope.launch {
//            if (repository.getCount() == 0) {  // add a simple suspend fun getCount() in repo/dao
//                addExamples()
//            }
//        }
    }

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
                    productPrice = product.defaultPrice.toString(),
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

        if (settingsUIState.value.productName.isBlank()) { userMessage = "'Name' field required" }

        val price = settingsUIState.value.productPrice
        val priceRegex = Regex("^\\d+(\\.\\d{1,2})?$")
        if (price.isNotBlank()) {
            if (!priceRegex.matches(price)) {
                userMessage = "${userMessage ?: ""} \n'Default Price' invalid. Must be a number with up to 2 decimals. Examples: 2.98, 2.1, 20, 0.2"
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

    /*// in SettingsViewModel
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val saleCount = saleRepository.countSalesForProduct(productId)
            if (saleCount > 0) {
                // show dialog (you already have the logic for this)
                // if user confirms → proceed
                repository.deleteProduct(productId) // cascade happens automatically
            } else {
                repository.deleteProduct(productId)
            }
        }
    }*/

    /*@Query("SELECT COUNT(*) FROM sales WHERE productId = :productId")
    suspend fun countSalesForProduct(productId: String): Int*/ //Todo add to settingsDao if the above used

    /*fun addExamples() {
        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 1",
                defaultPrice = 50.0,
                notes = "Test 1 product",
                orderIndex = 1
            )
        )

        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 2",
                defaultPrice = 2.0,
                notes = "Test 2 product",
                orderIndex = 2
            )
        )

        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 3",
                defaultPrice = 3.0,
                notes = "Test 3 product",
                orderIndex = 4
            )
        )

        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 4",
                defaultPrice = 40.0,
                notes = "Test 4 product",
                orderIndex = 4
            )
        )

        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 6",
                defaultPrice = 60.0,
                notes = "Test 6 product",
                orderIndex = 6
            )
        )

        saveProduct(
            ProductItem(
                id = UUID.randomUUID().toString(),
                name = "Product 7",
                defaultPrice = 70.0,
                notes = "Test 7 product",
                orderIndex = 7
            )
        )
    }*/
}
/*
2025-12-18 20:54:11.406  6490-6490  ArduinoASS              com.example.salestracker             D  Inside Launched effect Snackbar
2025-12-18 20:55:44.426  6490-6490  ArduinoSettings         com.example.salestracker             D  drawer save clicked
2025-12-18 20:55:44.426  6490-6490  ArduinoSettings         com.example.salestracker             D  User message is: null
---------------------------- PROCESS STARTED (6788) for package com.example.salestracker ----------------------------
2025-12-18 21:03:06.264  6788-6788  ArduinoASS              com.example.salestracker             D  Inside Launched effect Snackbar
2025-12-18 21:03:15.929  6788-6788  ArduinoSettings         com.example.salestracker             D  Products list is: [[]]
2025-12-18 21:03:15.929  6788-6788  ArduinoSettings         com.example.salestracker             D  products list is empty. adding some.
2025-12-18 21:03:15.931  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 1
2025-12-18 21:03:15.933  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 2
2025-12-18 21:03:15.934  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 3
2025-12-18 21:03:15.937  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 4
2025-12-18 21:03:15.937  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 6
2025-12-18 21:03:15.938  6788-6788  ArduinoSettings         com.example.salestracker             D  save product called with product Product 7
---------------------------- PROCESS ENDED (6788) for package com.example.salestracker ----------------------------
---------------------------- PROCESS STARTED (6835) for package com.example.salestracker ----------------------------
2025-12-18 21:03:54.557  6835-6835  ArduinoASS              com.example.salestracker             D  Inside Launched effect Snackbar
2025-12-18 21:03:57.733  6835-6835  ArduinoSettings         com.example.salestracker             D  Products list is: [[]]
2025-12-18 21:03:57.733  6835-6835  ArduinoSettings         com.example.salestracker             D  products list is empty. adding some.
2025-12-18 21:03:57.734  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 1
2025-12-18 21:03:57.737  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 2
2025-12-18 21:03:57.737  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 3
2025-12-18 21:03:57.737  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 4
2025-12-18 21:03:57.737  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 6
2025-12-18 21:03:57.738  6835-6835  ArduinoSettings         com.example.salestracker             D  save product called with product Product 7

 */