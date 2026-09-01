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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
    val salesUIState by viewModel.salesUIState.collectAsStateWithLifecycle()
    val isSelectionMode = salesUIState.selectedSaleEventIds.isNotEmpty()

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
                title = if (isSelectionMode) "${salesUIState.selectedSaleEventIds.size} Selected" else "All Sales",
                onNavigationIconClicked = onMenuClick,
                navigationIcon = {
                    if (isSelectionMode) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    } else {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Navigation Drawer")
                        }
                    }
                },
                colors = if (isSelectionMode) {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    TopAppBarDefaults.topAppBarColors()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            if (!isSelectionMode) {
                SaleFAB(
                    onFABClick = {
                        onAddEditSale(null)
                    },
                    icon = Icons.Filled.Add,
                    contentDescription = "Save Sale, Go to AllSales"
                )
            }
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

    BackHandler(enabled = salesUIState.selectedSaleEventIds.isNotEmpty()) {
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
                val isSelected = salesUIState.selectedSaleEventIds.contains(productSale.saleEvent.id)

                Box(
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                ) {
                    SalesListItem(
                        modifier = Modifier,
                        saleEventWithItems = productSale,
                        isSelected = isSelected,
                        onClick = {
                            if (salesUIState.selectedSaleEventIds.isNotEmpty()) {
                                viewModel.toggleSelection(productSale.saleEvent.id)
                            } else onAddEditSale(productSale)
                        },
                        onLongClick = { viewModel.toggleSelection(productSale.saleEvent.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(120.dp)) }
        }

        if (salesUIState.selectedSaleEventIds.isNotEmpty()) {
            if (!salesUIState.confirmDeletionValue) {
                if (salesUIState.selectedSaleEventIds.size < salesUIState.allSaleEventWithItems.size) {
                    Button(
                        onClick = { viewModel.selectAll(salesUIState.allSaleEventWithItems.map { it.saleEvent.id }) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                    ) {
                        Text("Select All (${salesUIState.allSaleEventWithItems.size})")
                    }
//                    Spacer(Modifier.height(4.dp))
                }
                Button(
                    onClick = { viewModel.askForDeleteConfirmation() }, //Todo all viewModelConfirmDeletion calls should be replaced with uiState.confirmDeletion
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 4.dp)
                ) {
                    Text("Delete selected (${salesUIState.selectedSaleEventIds.size})")
                }
            } else {
                Button(
                    onClick = { viewModel.deleteSelectedSales() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                ) {
                    Text("Confirm Deletion (${salesUIState.selectedSaleEventIds.size})")
                }

//                Spacer(Modifier.height(4.dp))
            }
            Button(
                onClick = { viewModel.clearSelection() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 4.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}