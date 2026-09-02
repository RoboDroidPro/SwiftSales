package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.SaleRepository
import com.example.salestracker.data.model.SaleEvent
import com.example.salestracker.data.model.SaleItem
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.navigation.ADD_RESULT_OK
import com.example.salestracker.ui.screens.sale.add.AddSaleAction
import com.example.salestracker.ui.screens.sale.add.AddSaleUIState
import com.example.salestracker.ui.screens.sale.add.SaleItemAction
import com.example.salestracker.ui.screens.sale.add.SaleItemState
import com.example.salestracker.utils.toSwiftCurrency
import com.example.salestracker.utils.toSwiftString
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
                _addSaleUIState.update {
                    it.copy(date = action.newDate)
                }
            }
            is AddSaleAction.BuyerChanged -> {
                _addSaleUIState.update { currentState ->
                    currentState.copy(
                        buyer = action.newBuyer,
                        buyerError = null
                    )
                }
            }

            is AddSaleAction.SaleNotesChanged -> {
                _addSaleUIState.update { currentState ->
                    currentState.copy(
                        saleNotes = action.newNotes
                    )
                }
            }
            AddSaleAction.AddSaleItem -> {
                addSaleItem()
            }
            is AddSaleAction.RemoveSaleItem -> {
                removeSaleItem(action.itemId)
            }

            is AddSaleAction.SaleEntryAction -> {
                updateSaleEntry(action.itemId, action.action)
            }

            AddSaleAction.SaveSale -> saveSale()
        }
    }

    private fun addSaleItem() {
        _addSaleUIState.update { currentState ->
            currentState.copy(
                saleItems = currentState.saleItems + SaleItemState(),
                itemsError = null
            )
        }
    }

    private fun removeSaleItem(itemId: String) {
        _addSaleUIState.update { currentState ->
            val newSaleItems = currentState.saleItems.filter { it.saleItemId != itemId }
            currentState.copy(
                totalSalePrice = calculateTotalSalePrice(newSaleItems) ?: "0.00",
                saleItems = newSaleItems
            )
        }
    }

    private fun updateSaleEntry(itemId: String, action: SaleItemAction) {
        _addSaleUIState.update { state ->
            val saleItems: List<SaleItemState> = state.saleItems.map { saleItemState ->
                if (saleItemState.saleItemId == itemId) {
                    when (action) {
                        is SaleItemAction.ProductChanged -> saleItemState.copy(
                            product = action.newProduct,
                            lineTotal = calculatePrice(
                                (saleItemState.quantity ?: 1),
                                action.newProduct.defaultPrice
                            ),
                            unitPrice = action.newProduct.defaultPrice.toSwiftString()
                        )
                        is SaleItemAction.UnitPriceChanged -> {
                            val productPrice = action.newPrice.toSwiftCurrency()
                            saleItemState.copy(
                                unitPrice = action.newPrice,
                                lineTotal = calculatePrice(
                                    saleItemState.quantity ?: 1,
                                    productPrice ?: 0
                                ),
                                unitPriceError = if (productPrice == null) "Invalid price" else null
                            )
                        }
                        is SaleItemAction.QuantityChanged -> {
                            val cleanedQty = action.newQuantity.replace(" 1", "")
                            Log.d(TAG, "cleanedQty: $cleanedQty")
                            saleItemState.copy(
                                quantity = cleanedQty.toIntOrNull(),
                                lineTotal = calculatePrice(
                                    cleanedQty.toIntOrNull() ?: 1,
                                    saleItemState.unitPrice.toSwiftCurrency() ?: 0
                                )
                            )
                        }
                    }
                } else saleItemState
            }
            state.copy(
                totalSalePrice = calculateTotalSalePrice(saleItems) ?: "0.00",
                saleItems = saleItems
            )
        }
    }

    private fun calculatePrice(quantity: Int, productPrice: Int): String {
        return (quantity * productPrice).toSwiftString()
    }

    private fun calculateTotalSalePrice(items: List<SaleItemState>): String? {
        return items.sumOf { it.lineTotal.toSwiftCurrency() ?: return null }.toSwiftString()
    }

    /*fun changeDate() {
        //Todo this is called when the user clicks the "date" field in addEdit
    }*/

    fun saveSale(){ // removed the boolean return, and its return statements below, because it is no longer necessary
        Log.d(TAG, "saveSale called")

        val buyerError = if (_addSaleUIState.value.buyer.isBlank()) "Buyer field required!" else null
        val itemsError = if (_addSaleUIState.value.saleItems.isEmpty()) "Add at least one item!" else null

        /*val priceRegex = Regex("^\\d+(\\.\\d{1,2})?$")
        if (!priceRegex.matches(priceStr)) {
            userMessage = "${userMessage ?: ""} 'Total Sale Price' invalid. Must be a number with up to 2 decimals. \nExamples: 2.98, 2.1, 20, 0.2"
        }*/
        Log.d(TAG, "ready to start if")

        if (buyerError == null &&
            itemsError == null
        ) {
            viewModelScope.launch {
                Log.d(TAG, "viewModelScope.launch called")
                val saleEventId = UUID.randomUUID().toString()

                // 1. Create the Header (Entity)
                val event = SaleEvent(
                    id = saleEventId,
                    date = uiState.value.date,
                    buyer = uiState.value.buyer,
                    totalSalePrice = uiState.value.totalSalePrice.toSwiftCurrency() ?: 0,
                    saleNotes = uiState.value.saleNotes
                )
                Log.d(TAG, "event created")

                // 2. Create the Items (List of Entities)
                val items = uiState.value.saleItems.map { itemState ->
                    SaleItem(
                        id = UUID.randomUUID().toString(),
                        saleId = saleEventId,
                        productId = itemState.product.id,
                        salePrice = itemState.lineTotal.toSwiftCurrency() ?: 0,
                        quantity = itemState.quantity ?: 1
                    )
                }
                Log.d(TAG, "items created")

                // 3. Save them separately via the repo
                saleRepository.insertSaleWithItems(event, items)
                Log.d(TAG, "saleRepository.insertSaleWithItems called")

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
            }
        } else {
            Log.d(TAG, "received errors: $buyerError, $itemsError")
            _addSaleUIState.update { currentState ->
                currentState.copy(
                    buyerError = buyerError,
                    itemsError = itemsError
                )
            }
        }
    }
}