package com.example.salestracker.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.ui.components.ProductDropdown
import com.example.salestracker.ui.components.SaleFAB
import com.example.salestracker.ui.components.SalesAppBar
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
            viewModel = viewModel,
            onNavigateToAllSales = onBackClicked,
        )
    }
}

@Composable
fun AddEditSale(
    modifier: Modifier = Modifier,
    viewModel: AddSaleViewModel,
    onNavigateToAllSales: () -> Unit
) {
    val addEditUIState by viewModel.uiState.collectAsStateWithLifecycle()
    val dropdownProducts by viewModel.availableProducts.collectAsStateWithLifecycle()

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        item {
            OutlinedTextField(
                value = addEditUIState.date,
                onValueChange = { /*viewModel.changeDate()*/ }, //this was commented out because if date is changeable, we need to parse it before storing it.
                label = { Text("Date") },
                readOnly = true, //Todo this needs to be false or deleted if a way of changing the date is implemented
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        // OutlinedTextField(value = viewModel.product.value, onValueChange = { viewModel.product.value = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        item {
            OutlinedTextField(
                value = addEditUIState.buyer,
                onValueChange = { viewModel.buyerChanged(it) },
                label = { Text("Buyer") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ProductDropdown(
                selectedProduct = addEditUIState.selectedProduct?.name ?: "",
                selectedProductChanged = { viewModel.selectedProductChanged(it) },
                productOptions = dropdownProducts,
            )
        }  //PRODUCT FIELD
        item {
            OutlinedTextField(
                value = if (addEditUIState.quantity != null) addEditUIState.quantity.toString() else "",
                onValueChange = { viewModel.quantityChanged(it.toIntOrNull()) },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                value = addEditUIState.totalSalePrice,
                onValueChange = { viewModel.totalSalePriceChanged(it) },
                label = { Text("Total Sale Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        item {
            OutlinedTextField(
                value = addEditUIState.notes,
                onValueChange = { viewModel.notesChanged(it) },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (addEditUIState.userMessage != null) {
            item {
                Text(
                    text = addEditUIState.userMessage!!,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToAllSales, modifier = Modifier.fillMaxWidth()) {
                Text("View All Sales")
            }
        }
    }
}

/*
@Composable
fun NewSaleScreen(
    viewModel: SaleViewModel,
    modifier: Modifier = Modifier,
    onNavigateToAllSales: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
            .fillMaxWidth()
    ) {
//        item {
//            Text("Add Sale", fontSize = 24.sp, fontWeight = FontWeight.Bold)
//            Spacer(modifier.height(12.dp))
//        }
        item {
            OutlinedTextField(
                value = viewModel.formatDateForDisplay(viewModel.date.value),
                onValueChange = { /*viewModel.date.value = it /*it refers to the value that changed*/*/ }, //this was commented out because if date is changeable, we need to parse it before storing it.
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        // OutlinedTextField(value = viewModel.product.value, onValueChange = { viewModel.product.value = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        item {
            OutlinedTextField(
                value = viewModel.buyer.value,
                onValueChange = { viewModel.buyer.value = it },
                label = { Text("Buyer") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
//            ProductDropdown(viewModel) No longer works because ProductDropdown accepts different params now
        }  //PRODUCT FIELD
        item {
            OutlinedTextField(
                value = viewModel.quantity.intValue.toString(),
                onValueChange = { newValue ->
                    viewModel.quantity.intValue = newValue.toIntOrNull() ?: 0
                },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                value = viewModel.totalSalePrice.value,
                onValueChange = { viewModel.totalSalePrice.value = it },
                label = { Text("Total Sale Price") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = viewModel.notes.value,
                onValueChange = { viewModel.notes.value = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                    if (
                        viewModel.buyer.value.isNotBlank()
                        && viewModel.selectedProduct.value != Product.None
                        && viewModel.totalSalePrice.value.isNotBlank()
                    ) {
                        viewModel.saveSale()
                        Toast.makeText(
                            context,
                            "${viewModel.selectedProduct.value.displayName} sold to ${viewModel.buyer.value}. Total Sale Price: ${viewModel.totalSalePrice.value} Sale saved.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "FIELD REQUIRED. Sale must have buyer AND product. NOT SAVED",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Sale")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToAllSales, modifier = Modifier.fillMaxWidth()) {
                Text("View All Sales")
            }
        }
    }
}
 */