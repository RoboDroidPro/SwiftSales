package com.example.salestracker.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.SalesListItem
import com.example.salestracker.data.model.SaleEventWithItems
import com.example.salestracker.ui.components.SaleFAB
import com.example.salestracker.ui.components.SalesAppBar
import com.example.salestracker.ui.navigation.SNACKBAR_MSG_KEY
import com.example.salestracker.viewModel.AllSalesViewModel

private const val TAG = "ArduinoASS"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSalesScreen(
    modifier: Modifier = Modifier,
    onAddEditSale: (SaleEventWithItems?) -> Unit,
    onMenuClick: () -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: AllSalesViewModel = hiltViewModel(),
    savedStateHandle: SavedStateHandle
) {

    /**
     * Final step: Show a one-time "Sale saved" / "Sale edited" snackbar when returning from AddEditSaleScreen
     *
     * This LaunchedEffect observes that exact SavedStateHandle as a StateFlow.
     *   • As soon as the flow emits a non-null resource ID, we:
     *       – Show the snackbar with the corresponding string
     *       – Immediately clear the key (set to null)
     *
     * Why this is safe and perfect:
     *   • The value is emitted immediately on recomposition → snackbar appears reliably
     *   • Clearing the key guarantees the message is shown exactly once
     *   • No SharedFlow replay / buffer / timing issues
     *   • Survives configuration changes (SavedStateHandle is persisted)
     *   • Zero extra events or ViewModel indirection needed
     *
     * This is the Google-recommended pattern for returning simple one-time results
     * (including feedback messages) between Compose navigation destinations.
     */

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Log.d(TAG, "Inside Launched effect Snackbar")
        savedStateHandle.getStateFlow<Int?>(SNACKBAR_MSG_KEY, null).collect { msgRes ->
            if (msgRes != null) {
                Log.d(TAG, "allSales snackbar event collected: $msgRes")
                snackBarHostState.showSnackbar(context.getString(msgRes))
                savedStateHandle[SNACKBAR_MSG_KEY] = null
            }
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SalesAppBar(
                "All Sales",
                onMenuClick,
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            SaleFAB(
                onFABClick = {
                    onAddEditSale(null)
                },
                icon = Icons.Filled.Add,
                contentDescription = "Save Sale, Go to AllSales"
            )
        },
    ) { paddingValues ->
        AllSales(
            modifier = modifier.padding(paddingValues),
            onAddEditSale = onAddEditSale,
            viewModel = viewModel
        )
    }
}

@Composable
fun AllSales(
    modifier: Modifier = Modifier,
    viewModel: AllSalesViewModel,
    onAddEditSale: (SaleEventWithItems?) -> Unit,
) {
    val salesUIState by viewModel.salesUIState.collectAsStateWithLifecycle()

    BackHandler(enabled = salesUIState.selectedSaleEventWithItems.isNotEmpty()) {
        viewModel.clearSelection()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)

        ) {
            items(salesUIState.allSaleEventWithItems) { productSale ->
                val isSelected = salesUIState.selectedSaleEventWithItems.contains(productSale)

                Box(
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                        else Color.Transparent
                    )
                ) {
                    SalesListItem(
                        modifier = Modifier,
                        saleEventWithItems = productSale,
                        onClick = { if (salesUIState.selectedSaleEventWithItems.isNotEmpty()) {
                            viewModel.toggleSelection(productSale)
                        }
                        else onAddEditSale(productSale) },
                        onLongClick = { viewModel.toggleSelection(productSale) }
                    )
                }
            }
        }


        if (salesUIState.selectedSaleEventWithItems.isNotEmpty()) {

            if (salesUIState.selectedSaleEventWithItems.size < salesUIState.allSaleEventWithItems.size) {
                Button(
                    onClick = { viewModel.selectAll(salesUIState.allSaleEventWithItems) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select All (${salesUIState.allSaleEventWithItems.size})")
                }
                Spacer(Modifier.height(8.dp))
            }

            if (!salesUIState.confirmDeletionValue) {
                Button(
                    onClick = { viewModel.askForDeleteConfirmation() }, //Todo all viewModelConfirmDeletion calls should be replaced with uiState.confirmDeletion
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete selected (${salesUIState.selectedSaleEventWithItems.size})")
                }
            } else {
                Button(
                    onClick = { viewModel.deleteSelectedSales() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Deletion (${salesUIState.selectedSaleEventWithItems.size})")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.cancelDeletionConfirmation() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.clearSelection() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Selection")
            }
        }
    }
}