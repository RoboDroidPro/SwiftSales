package com.example.salestracker.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.data.model.Product
import com.example.salestracker.ui.components.DeleteDialog
import com.example.salestracker.ui.components.SalesAppBar
import com.example.salestracker.utils.toSwiftString
import com.example.salestracker.viewModel.SettingsViewModel

const val SETTAG = "ArduinoSettings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // This boolean lives in the ViewModel – pure UI state, zero business logic
    val showAddProductSheet by viewModel.showAddProductSheet.collectAsStateWithLifecycle()
    val productsList by viewModel.productsList.collectAsStateWithLifecycle()
    val uIState by viewModel.settingsUIState.collectAsStateWithLifecycle()

    // Show/hide the sheet based on ViewModel flag
    LaunchedEffect(showAddProductSheet) {
        if (showAddProductSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    if (showAddProductSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onSheetDismissed() },
            sheetState = sheetState,
        ) {
            AddProductSheet(
                uIState = uIState,
                onAction = viewModel::onAction,
            )
        }
    }

    Scaffold(
        topBar = {
            SalesAppBar(
                title = "Settings",
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Settings(
            modifier = Modifier.padding(paddingValues),
            productsList = productsList,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    productsList: List<Product> = emptyList(),
    onAction: (SettingsAction) -> Unit = {},
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }  // Local UI state for dialog

    if (showDeleteAllDialog) {
        DeleteDialog(
            title = "Delete All",
            contentText = "Are you sure you want to delete ALL the products? Only products with NO SALE records can be deleted.",
            onConfirm = {
                showDeleteAllDialog = false
                onAction(SettingsAction.DeleteAllProducts)
            },
            confirmText = "Delete",
            onCancel = { showDeleteAllDialog = false }
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Here you can Add/Remove/Edit your products. Each of the Products" +
                    " listed here will appear in the Add Sale screen's Product field dropdown. The price is optional." +
                    " It will be used to autofill the 'Price' field when you add a sale. " +
                    "'Price' field will not autofill, or will be inaccurate if you don't specify it here. " +
                    "Click any product to view its details, or edit it." +
                    " To delete, click the product, then in the edit screen, click the trash icon.",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(
                    width = 2.dp,  // reduced from 4.dp — looks cleaner in M3
                    color = MaterialTheme.colorScheme.outlineVariant,  // subtle border, adapts to theme
                    shape = RoundedCornerShape(12.dp)  // slightly larger radius for modern feel
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,  // ← THE FIX
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp) // inner padding
        ) {
            items(productsList) { product ->
                ProductCard(
                    product,
                    onClick = { onAction(SettingsAction.AddEditProduct(product)) }
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            onClick = { showDeleteAllDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(
                text = "Delete All",
                fontSize = 16.sp
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            onClick = { onAction(SettingsAction.AddEditProduct(null)) }
        ) {
            Text(
                text = "Add Product",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(50.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$${product.defaultPrice.toSwiftString()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W800,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
//    SettingsScreen {  }
}


/*BottomSheetScaffold(
      sheetContent = {
          AddProductSheet()
      },
//        sheetContentColor = Color.Blue,
      sheetPeekHeight = 100.dp,
      topBar = {
          SalesAppBar(
              title = "Settings",
              navigationIcon = {
                  IconButton(onClick = onBackClicked) {
                      Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                  }
              }
          )
      }
  ) {
      Settings(settingsViewModel = viewModel)
  }*/