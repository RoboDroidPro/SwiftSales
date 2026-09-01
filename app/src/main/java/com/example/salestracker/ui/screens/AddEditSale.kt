package com.example.salestracker.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.data.model.Product
import com.example.salestracker.toCurrencyString
import com.example.salestracker.ui.components.SaleFAB
import com.example.salestracker.ui.components.SalesAppBar
import com.example.salestracker.ui.screens.sale.add.AddSaleAction
import com.example.salestracker.ui.screens.sale.add.AddSaleUIState
import com.example.salestracker.ui.screens.sale.add.SaleItemCard
import com.example.salestracker.viewModel.AddSaleViewModel
import com.example.salestracker.viewModel.UIEvent

private const val TAG = "ArduinoAESS"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSaleScreen(
    modifier: Modifier = Modifier,
    onNavigateToAllSales: (String) -> Unit, //accepts a success code string
    viewModel: AddSaleViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClicked: () -> Unit,
    screenTitle: String = "Add Edit Sale"
) {

    val addEditUIState by viewModel.uiState.collectAsStateWithLifecycle()
    val dropdownProducts by viewModel.availableProducts.collectAsStateWithLifecycle()

    /**
     * Step #3.
     * Listens to/collects the flow.
     */
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.addEditEvents.collect { event ->
            Log.d(TAG, "AddEditEvent collection: [$event]")
            when(event) {
                is UIEvent.ShowSnackbar -> { //this could be used to display things like "Buyer field required"
                    // If you want inline snackbars on AddEditScreen
                    snackBarHostState.showSnackbar(
                        context.getString(event.messageRes)
                    )
                    Log.d(TAG, "addEditEvent is show snackbar '${context.getString(event.messageRes)}'")
                }
                /**
                 * This below contains the navigateBack event sent from [AddSaleViewModel.saveSale]
                 * Here we pass the result sent by saveSale as a param to the lambda param
                 * [onNavigateToAllSales],calling step 4# at [com.example.salestracker.ui.navigation.SaleNavGraph]
                 * in the AddEditSaleScreen call in the composable{} block
                 */
                is UIEvent.NavigateBack -> {
                    onNavigateToAllSales(event.resultCode)   //calls onNavigate passing the result code passed from the event flow
                    Log.d(TAG, "addEditEvent is nav back '${event.resultCode}'")
                }
            }
        }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SalesAppBar(
                screenTitle,
                onBackClicked,
                navigationIcon = { IconButton(onClick = onBackClicked) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            SaleFAB(
                onFABClick = {
                    Log.d("ArduinoNewSale", "AddEdit.FabClick")
                    viewModel.saveSale() // saveSale() starts the navigation to allSales.
                    //thus, we only need to call save.
                },
                icon = Icons.Filled.DoneOutline,
                contentDescription = "Save Sale, Go to AllSales"
            )
        },
    ) { paddingValues ->
        AddEditSale(
            modifier = modifier.padding(paddingValues),
            state = addEditUIState,
            onAction = viewModel::onAction,
            dropdownProducts = dropdownProducts,
            onNavigateToAllSales = onBackClicked,
        )
    }
}

@Composable
fun AddEditSale(
    modifier: Modifier = Modifier,
    state: AddSaleUIState = AddSaleUIState(),
    onAction: (AddSaleAction) -> Unit,
    dropdownProducts: List<Product>,
    onNavigateToAllSales: () -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        item {
            OutlinedTextField(
                value = state.date,
                onValueChange = { /*viewModel.changeDate()*/ }, //this was commented out because if date is changeable, we need to parse it before storing it.
                label = { Text("Date") },
                readOnly = true, //Todo this needs to be false or deleted if a way of changing the date is implemented
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = state.buyer,
                onValueChange = { onAction(AddSaleAction.BuyerChanged(it)) },
                label = { Text("Buyer") }, //todo I removed a modifier fill max. see if that makes trouble
                isError = state.buyerError != null, // Highlights the box in red
                supportingText = {
                    state.buyerError?.let { Text(it) } // Shows the error message below the box
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = state.totalSalePrice.toCurrencyString(),
                onValueChange = {},
                label = { Text("Total Sale Price") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = state.saleNotes,
                onValueChange = { onAction(AddSaleAction.SaleNotesChanged(it)) },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(state.saleItems) { saleItem ->
            SaleItemCard(
                itemState = saleItem,
                productOptions = dropdownProducts,
                onAction = onAction,
                cardNumber = state.saleItems.indexOf(saleItem) + 1
            )
        }

        if (state.itemsError != null) {
            item {
                Text(
                    text = state.itemsError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        item {
            Button(
                onClick = { onAction(AddSaleAction.AddSaleItem) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Product to Sale")
            }
        }

        /*
        // Inside the Column of SalesListItem
saleEventWithItems.items.forEach { itemWithProduct ->
    Text(
        text = "• ${itemWithProduct.saleItem.quantity}x ${itemWithProduct.product.name}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
         */
    }
}