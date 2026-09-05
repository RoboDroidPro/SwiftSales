package com.example.salestracker.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.Sale
import com.example.salestracker.SaleRepository
import com.example.salestracker.data.model.ProductItem
import com.example.salestracker.data.model.ProductSale
import com.example.salestracker.data.repository.ProductRepository
import com.example.salestracker.ui.navigation.ADD_RESULT_OK
import com.example.salestracker.ui.navigation.EDIT_RESULT_OK
import com.example.salestracker.ui.navigation.SaleDestinationsArgs
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
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

private const val TAG = "ArduinoAESVM"

data class AddEditState(
    val buyer: String = "",
    val selectedProduct: ProductItem? = null,
    val quantity: Int? = 0,
    val totalSalePrice: String = "",
    val notes: String = "",
    val date: String = LocalDate.now().toString(),
    val userMessage: String? = null
)

@HiltViewModel
class AddEditSaleVM @Inject constructor(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val saleId: String? = savedStateHandle[SaleDestinationsArgs.SALE_ID_ARG]

    private val _addEditState = MutableStateFlow(AddEditState())
    val uiState: StateFlow<AddEditState> = _addEditState.asStateFlow()

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

    init {
//        viewModelScope.launch {
//            _productList = saleRepository.getProductList()
//        }

        if (saleId != null) {
            //call the loadSale
            viewModelScope.launch {
                displaySale(saleRepository.getSaleById(saleId))
            }
        }
    }

    private fun displaySale(productSale: ProductSale?) {
        if (productSale != null) {
            _addEditState.update { currentState ->
                currentState.copy(
                    buyer = productSale.sale.buyer,
                    selectedProduct = productSale.product,
                    quantity = productSale.sale.quantity,
                    totalSalePrice = productSale.sale.totalSalePrice.toString(),
                    notes = productSale.sale.saleNotes ?: "",
                    date = productSale.sale.date
                )
            }
        }
    }

    fun buyerChanged(newValue: String) {
        _addEditState.update { currentState ->
            currentState.copy(
                buyer = newValue
            )
        }
    }

    fun selectedProductChanged(newProduct: ProductItem) {
        _addEditState.update { currentState ->
            currentState.copy(
                selectedProduct = newProduct,
                totalSalePrice = calculatePrice(productPrice = newProduct.defaultPrice)
            )
        }
    }

    fun quantityChanged(newQuantity: Int?) {
        _addEditState.update { currentState ->
            currentState.copy(
                quantity = newQuantity,
                totalSalePrice = calculatePrice(quantity = newQuantity ?: 0)
//                viewModel.quantity.intValue = newValue.toIntOrNull() ?: 0
            )
        }
    }

    fun totalSalePriceChanged(newSalePrice: String) {
        _addEditState.update { currentState ->
            currentState.copy(
                totalSalePrice = newSalePrice
            )
        }
    }

    fun notesChanged(newNotesValue: String) {
        _addEditState.update { currentState ->
            currentState.copy(
                notes = newNotesValue
            )
        }
    }

    private fun calculatePrice(quantity: Int? = null, productPrice: Double? = null): String {
        val price: Double = (quantity?.toDouble() ?: (uiState.value.quantity?.toDouble() ?: 0.0)) *
            (productPrice ?: (uiState.value.selectedProduct?.defaultPrice ?: 0.0))

        return if (price != 0.0) price.toString() else ""
    }

//    val testNullProductItem = ProductItem(
//        name = "ERROR",
//        defaultPrice = 0.0,
//        notes = "This is an error. Debugging needed",
//        orderIndex = 1
//    )

    /*fun changeDate() {
        //Todo this is called when the user clicks the "date" field in addEdit
    }*/

    fun saveSale(){ // removed the boolean return, and its return statements below, because it is no longer necessary
        var userMessage: String? = null
        if (uiState.value.buyer.isBlank()) userMessage = "'Buyer' field required!"
        if (uiState.value.selectedProduct == null) userMessage = "${userMessage ?: ""} \n'Product' field required!"
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
            _addEditState.update { currentState ->
                currentState.copy(
                    userMessage = userMessage
                )
            }
        } else {
            viewModelScope.launch {
                Log.d(TAG , "sale id is: $saleId")
                saleRepository.upsertSale(
                    Sale( //todo we need to somehow get the productId for product field
                        id = saleId ?: UUID.randomUUID().toString(), //generate the id here so it is not optional.
                        date = _addEditState.value.date, //todo these should perhaps be uiState.value instead
//                        product = _addEditState.value.selectedProduct!!,
                        productId = _addEditState.value.selectedProduct!!.id,
                        buyer = _addEditState.value.buyer,
                        quantity = _addEditState.value.quantity ?: 0,
                        totalSalePrice = priceStr.toDouble(),
                        saleNotes = _addEditState.value.notes
                    )
                )
                /**
                 * To see the sequence that goes through from the save button clicked, to the snackbar
                 * displayed, follow the numbers #1.
                 * This is number #1. Next is in [_addEditEvents]
                 */
                _addEditEvents.emit(  //emits a event into the flow for AddEditSale to collect
                    UIEvent.NavigateBack(
                        resultCode = if (saleId == null) ADD_RESULT_OK else EDIT_RESULT_OK //passing the success of the save. Edit, or add
                    )
                )
                Log.d(TAG, "addEditEvents.emitted result: ${if (saleId == null) ADD_RESULT_OK else EDIT_RESULT_OK}")
            }
        }
    }
}