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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.salestracker.data.model.ProductItem
import com.example.salestracker.ui.components.DeleteDialog
import com.example.salestracker.ui.components.SalesAppBar
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
    //        dragHandle = null
        ) {
            AddProductSheet(
                viewModel = viewModel
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
            settingsViewModel = viewModel
        )
    }
}

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {

//    val settingsUIState by settingsViewModel.settingsUIState.collectAsStateWithLifecycle()
    val productsList by settingsViewModel.productsList.collectAsStateWithLifecycle()
    var showDeleteAllDialog by remember { mutableStateOf(false) }  // Local UI state for dialog

    if (showDeleteAllDialog) {
        DeleteDialog(
            title = "Delete All",
            contentText = "Are you sure you want to delete ALL the products? This will also delete ALL the sales recorded.",
            onConfirm = settingsViewModel::deleteAll,
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
//            fontSize = 20.sp,
//            fontWeight = FontWeight.W300,
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
                    onClick = { settingsViewModel.addEditProduct(product) }
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            onClick = { showDeleteAllDialog = true }
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
            onClick = { settingsViewModel.addEditProduct(null) }
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
    product: ProductItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(50.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
/*        colors = CardColors(
            Color.LightGray,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.DarkGray
        )*/
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
                fontSize = 16.sp
            )
            Text(
                text = "$${product.defaultPrice}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W800
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