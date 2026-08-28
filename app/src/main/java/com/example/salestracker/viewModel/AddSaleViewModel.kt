package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.SaleEvent
import com.example.salestracker.SaleRepository
import com.example.salestracker.data.model.SaleItem
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.navigation.ADD_RESULT_OK
import com.example.salestracker.ui.screens.sale.add.AddSaleAction
import com.example.salestracker.ui.screens.sale.add.AddSaleUIState
import com.example.salestracker.ui.screens.sale.add.SaleItemAction
import com.example.salestracker.ui.screens.sale.add.SaleItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val TAG = "ArduinoAESVM"

@HiltViewModel
class AddSaleViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

//    private val saleId: String? = savedStateHandle[SaleDestinationsArgs.SALE_ID_ARG]

    private val _addSaleUIState = MutableStateFlow(AddSaleUIState())
    val uiState: StateFlow<AddSaleUIState> = _addSaleUIState.asStateFlow()

    /**
     * variable to share a flow of events of the AddEditScreen. AddEditScreen listens to this flow
     * the [saveSale] function uses it to emit a snackbar.
     * This is #2 of snackbar journey. Next is in
     * @see com.example.salestracker.ui.screens.AddEditSaleScreen
     */
    private val _addEditEvents = MutableSharedFlow<UIEvent>()
    val addEditEvents = _addEditEvents.asSharedFlow()

    // AddEditSaleViewModel – inject ProductRepository only
    val availableProducts = productRepository.productsList.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),  // keeps flowing while screen subscribed (stops 5s after unsubscribed)
        initialValue = emptyList()                      // shown immediately before DB loads
    )

    fun onAction(action: AddSaleAction) {
        when (action) {
            is AddSaleAction.DateChanged -> {
                //todo date change
            }
            is AddSaleAction.BuyerChanged -> {
                buyerChanged(action.newBuyer)
            }
            is AddSaleAction.TotalSalePriceChanged -> {
                totalSalePriceChanged(action.newSalePrice)
            }
            is AddSaleAction.SaleNotesChanged -> {
                notesChanged(action.newNotes)
            }
            AddSaleAction.AddSaleItem -> {
                addSaleItem()
            }
            is AddSaleAction.RemoveSaleItem -> {
                removeSaleItem(action.itemId)
            }

            is AddSaleAction.SaleEntryAction -> updateSaleEntry(action.itemId, action.action)

            AddSaleAction.SaveSale -> saveSale()
        }
    }

    private fun addSaleItem() {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                saleItems = currentState.saleItems + SaleItemState()
            )
        }
    }

    private fun removeSaleItem(itemId: String) {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                saleItems = currentState.saleItems.filter { it.saleItemId != itemId }
            )
        }
    }

    private fun updateSaleEntry(itemId: String, action: SaleItemAction) {
        _addSaleUIState.update { state ->
            state.copy(saleItems = state.saleItems.map { saleItemState ->
                if (saleItemState.saleItemId == itemId) {
                    when (action) {
                        is SaleItemAction.ProductChanged -> saleItemState.copy(product = action.newProduct)
                        is SaleItemAction.ProductPriceChanged -> saleItemState.copy(salePrice = action.newPrice.toDoubleOrNull()?: 0.0)
                        is SaleItemAction.QuantityChanged -> saleItemState.copy(
                            quantity = action.newQuantity.toIntOrNull()
                        )
                    }
                } else saleItemState
            })
        }
    }

    private fun buyerChanged(newValue: String) {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                buyer = newValue
            )
        }
    }

    private fun totalSalePriceChanged(newSalePrice: String) {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                totalSalePrice = newSalePrice
            )
        }
    }

    private fun notesChanged(newNotesValue: String) {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                saleNotes = newNotesValue
            )
        }
    }

//    private fun calculatePrice(quantity: Int? = null, productPrice: Double? = null): String {
//        val price: Double = (quantity?.toDouble() ?: (uiState.value.quantity?.toDouble() ?: 0.0)) *
//            (productPrice ?: (uiState.value.selectedProduct?.defaultPrice ?: 0.0))
//
//        return if (price != 0.0) price.toString() else ""
//    }

    /*fun changeDate() {
        //Todo this is called when the user clicks the "date" field in addEdit
    }*/

    fun saveSale(){ // removed the boolean return, and its return statements below, because it is no longer necessary
        var userMessage: String? = null
        if (uiState.value.buyer.isBlank()) userMessage = "'Buyer' field required!"
        if (uiState.value.saleItems.isEmpty()) userMessage = "${userMessage ?: ""} \n'Product' field required!"
        val priceStr = uiState.value.totalSalePrice

        if (priceStr.isBlank()) {
            userMessage = "${userMessage ?: ""} \n'Price' field required! "
        } else {
            val priceRegex = Regex("^\\d+(\\.\\d{1,2})?$")
            if (!priceRegex.matches(priceStr)) {
                userMessage = "${userMessage ?: ""} \n'Total Sale Price' invalid. Must be a number with up to 2 decimals. \nExamples: 2.98, 2.1, 20, 0.2"
            }
        }

        if (userMessage != null) {
            _addSaleUIState.update { currentState ->
                currentState.copy(
                    userMessage = userMessage
                )
            }
        } /*else {*/
            viewModelScope.launch {
                val saleId = UUID.randomUUID().toString()

                // 1. Create the Header (Entity)
                val event = SaleEvent(
                    id = saleId,
                    date = uiState.value.date,
                    buyer = uiState.value.buyer,
                    totalSalePrice = priceStr.toDoubleOrNull() ?: 0.0,
                    saleNotes = uiState.value.saleNotes
                )

                // 2. Create the Items (List of Entities)
                val items = uiState.value.saleItems.map { itemState ->
                    SaleItem(
                        id = UUID.randomUUID().toString(),
                        saleId = saleId,
                        productId = itemState.product.id,
                        salePrice = itemState.salePrice,
                        quantity = itemState.quantity ?: 0
                    )
                }

                // 3. Save them separately via the repo
                saleRepository.upsertSaleWithItems(event, items)
                /**
                 * To see the sequence that goes through from the save button clicked, to the snackbar
                 * displayed, follow the numbers #1.
                 * This is number #1. Next is in [_addEditEvents]
                 */
                _addEditEvents.emit(  //emits an event into the flow for AddEditSale to collect
                    UIEvent.NavigateBack(
                        resultCode = ADD_RESULT_OK
                    )
                )
                Log.d(TAG, "addEditEvents.emitted result: $ADD_RESULT_OK")
//            }
        }
    }
}