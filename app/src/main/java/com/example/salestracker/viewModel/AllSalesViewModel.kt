package com.example.salestracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.salestracker.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ArduinoASVM"

/**
 * Immutable UI state for the AllSales screen.
 *
 * The ViewModel exposes this as a StateFlow<SalesUIState>. Whenever we need
 * to change something in the state, we call `salesUIState.update { current ->
 *     current.copy(...)
 * }`.
 *
 * Why `copy()`?
 * - The UI state is immutable, so creating a new instance is the only way to update it.
 * - A new instance causes the StateFlow to emit, which notifies Compose that the
 *   UI state changed and triggers recomposition.
 *
 * Using a single immutable data class for UI state ensures:
 * - predictability (state is always consistent)
 * - easier testing
 * - safer updates (no mutable UI data spread around)
 *
 * Why use it.copy()? Why not make the SalesUIState properties var, and just modify them?
 * The answer is: UIState is a flow. It will not emit anything unless a new instance is given it.
 * So if we just UIState.selectedSales = someDifferentList, the instance doesn't change, and the
 * flow does not emit again, and the listening/collecting side never finds out there was a change
 * and thus does not recompose.
 */
data class SalesUIState(
    val allSaleWithItems: List<com.example.salestracker.data.model.SaleWithItems> = emptyList(),
    val selectedSaleWithItems: List<com.example.salestracker.data.model.SaleWithItems> = emptyList(),
    val confirmDeletionValue: Boolean = false,
)

@HiltViewModel
class AllSalesViewModel @Inject constructor(
    private val saleRepo: SaleRepository,
) : ViewModel() {

    private val _salesUIState = MutableStateFlow(SalesUIState())
    val salesUIState = _salesUIState.asStateFlow()

    // AllSalesViewModel Todo this is better than init{} system currently running
    val salesWithProducts = saleRepo.allSales.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),  // keeps flowing while screen subscribed (stops 5s after unsubscribed)
        initialValue = emptyList()                      // shown immediately before DB loads
    )

    init {
        observeSales() // this is called at initialization so that it will load the sales list
    }

    // listens to the allSales flow of list of sales
    private fun observeSales() {
        viewModelScope.launch {
            saleRepo.allSales.collect { sales ->
                _salesUIState.update { it.copy(allSaleWithItems = sales) } //Todo expecting ProductSale, gets Sale
            }
        }
    }

    // this fun deselects all sales
    private fun deselect() {
        _salesUIState.update { currentState ->
            currentState.copy(
                selectedSaleWithItems = emptyList(),
                confirmDeletionValue = false
            )
        }
    }

    // toggles the selection of the sale provided in the param
    fun toggleSelection(saleWithItems: com.example.salestracker.data.model.SaleWithItems) {
        _salesUIState.update { uIState -> //update the salesUIState, making its users get recomposed
            val newSelectedList =
                if (saleWithItems in uIState.selectedSaleWithItems)
                    uIState.selectedSaleWithItems - saleWithItems
                else
                    uIState.selectedSaleWithItems + saleWithItems

            uIState.copy(selectedSaleWithItems = newSelectedList) //copy the old instance of salesUIState,
            // only changing the "selected sales" field
        }
    }

    fun askForDeleteConfirmation() {
        _salesUIState.update { currentState ->
            currentState.copy(
                confirmDeletionValue = true
            )
        }
    }

    /**
     * The only purpose of this fun is to cancel the deletion,
     * thus making the Yes, Delete button disappear, but not deselecting
     * the selected sales. If it is desired that it deselect also, call [deselect]
     */
    fun cancelDeletionConfirmation() {
        _salesUIState.update { currentState ->
            currentState.copy(
                confirmDeletionValue = false
            )
        }
    }

    //calls saleRepo.deleteSales on all the selected sales in the salesUIState
    // called when the deletion of selected sales is confirmed ("Confirm Deletion" button clicked)
    fun deleteSelectedSales() {
//        viewModelScope.launch {
//            saleRepo.deleteSales(salesUIState.value.selectedSaleWithItems.toSaleList())
//            deselect()
//        }
    }

    // Clears the selected sales, by removing all the sales from selectedSales List<Sale>
    fun clearSelection() {
        deselect()
    }

    fun selectAll(saleWithItems: List<com.example.salestracker.data.model.SaleWithItems>) {
        _salesUIState.update { currentState ->
            currentState.copy(
                selectedSaleWithItems = saleWithItems
            )
        }
    }
/*

      This is now step #6. Here we receive a stringRes and emit a snackBarEvent to
     * [com.example.salestracker.ui.screens.AllSalesScreen]. Next step is [_allSaleEvents],
     * where these events get exposed

    fun emitSnackbar(@StringRes msgRes: Int) {
        Log.d(TAG, "emitSnackBar called with a messageRes")
        viewModelScope.launch {
            _allSaleEvents.tryEmit(UIEvent.ShowSnackbar(msgRes))
        }
    }*/
}


/*fun SaleWithItems.toSale() =
    Sale(
        id = sale.id,
        date = sale.date,
        buyer = sale.buyer,
        totalSalePrice = sale.totalSalePrice,
        saleNotes = sale.saleNotes
    )

fun List<SaleWithItems>.toSaleList() =
    map(SaleWithItems::toSale)*/

/*
viewModelScope.launch {// Observe navigation results here!
            savedStateHandle.getStateFlow<String?>(SALE_RESULT_KEY, null).collect { result ->
                if (result != null) {
                    when (result) {
                        ADD_RESULT_OK -> _allSaleEvents.emit(UIEvent.ShowSnackbar(R.string.save_success))
                        EDIT_RESULT_OK -> _allSaleEvents.emit(UIEvent.ShowSnackbar(R.string.edit_success))
                    }
                    savedStateHandle[SALE_RESULT_KEY] = null
                }
            }
        }
 */