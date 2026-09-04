package com.example.salestracker.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salestracker.ui.components.DeleteDialog
import com.example.salestracker.ui.components.StockStatusRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductSheet(
    uIState: SettingsUIState,
    onAction: (SettingsAction) -> Unit,
) { // Bottom drawer
    var showDeleteDialog by remember { mutableStateOf(false) }  // Local UI state for dialog

    if (showDeleteDialog) {
        DeleteDialog(
            title = "Delete Product?",
            contentText = "Are you sure you want to delete this product? Only products with NO SALE RECORDS can be deleted.",
            onConfirm = {
                showDeleteDialog = false
                onAction(SettingsAction.DeleteProduct)
            },
            confirmText = "Delete",
            onCancel = { showDeleteDialog = false}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(700.dp)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if(uIState.showDelete) "Edit Product" else "Add Product",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        OutlinedTextField(
            value = uIState.productName,
            onValueChange = {
                if (it.length <= 40) onAction(SettingsAction.ProductNameChanged(it))
            },
            label = { Text("Product Name") },
            supportingText = {
                Text(
                    text = uIState.productError ?: "${uIState.productName.length} / 40",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (uIState.productError == null) TextAlign.End else null,
                )
            },
            singleLine = true,
            isError = uIState.productError != null,
            modifier = Modifier
                .fillMaxWidth()
        )

        OutlinedTextField(
            value = uIState.productPrice,
            onValueChange = { onAction(SettingsAction.ProductPriceChanged(it)) },
            label = { Text("Default Price") },
            modifier = Modifier.fillMaxWidth(),
            isError = uIState.priceError != null,
            supportingText = {
                uIState.priceError?.let { Text(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = uIState.productNotes,
            onValueChange = { onAction(SettingsAction.ProductNotesChanged(it)) },
            label = { Text("Product Notes") },
            modifier = Modifier
                .fillMaxWidth()
        )

        StockStatusRow(
            inStock = uIState.productInStock,
            onCheckedChange = { onAction(SettingsAction.ProductInStockToggle) }
        )

        OutlinedTextField(
            value = uIState.productOrderIndex,
            onValueChange = { onAction(SettingsAction.ProductIndexChanged(it)) },
            label = { Text("Order Index") },
            modifier = Modifier.fillMaxWidth(),
            isError = uIState.indexError != null,
            supportingText = {
                uIState.indexError?.let { Text(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Text(
            text = "NOTE: This field is for ordering. The higher the number entered, the lower on the list it will appear. Number 1 goes at the top.",
            fontSize = 16.sp,
            fontWeight = FontWeight.W600
        )

        Row {
            Button(
                onClick = { onAction(SettingsAction.SaveProductClicked) }
            ) {
                Row {
                    Text(text = "Save")
                    Icon(imageVector = Icons.Filled.DoneOutline, contentDescription = "Save")
                }
            }

            if (uIState.showDelete) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(start = 16.dp)
                    )
                {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Product",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (uIState.deleteProductError != null) {
            Text(
                text = uIState.deleteProductError,
                fontWeight = FontWeight.W800,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 24.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun SettingsDrawerPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
//        SettingsScreen {  }
    }
}