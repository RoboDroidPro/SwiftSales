package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.salestracker.R
import com.example.salestracker.data.model.Product
import androidx.compose.material3.MenuAnchorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDropdown(
    selectedProduct: String,
    selectedProductChanged: (Product) -> Unit,
    isDropdownError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    productOptions: List<Product>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedProduct,
            onValueChange = {},
            label = { Text(stringResource(R.string.product_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            isError = isDropdownError, // Highlights the box in red
            supportingText = supportingText,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (productOptions.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.no_products_dropdown_placeholder),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                productOptions.forEach { product ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (product.inStock) product.name else "${product.name} ${stringResource(R.string.out_of_stock_suffix)}",
                                color = if (product.inStock) {
                                    MaterialTheme.colorScheme.onSurface  // Normal text color
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant  // Greyed out (perfect M3 gray!)
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            selectedProductChanged(product)
                            expanded = false
                        },
                        enabled = product.inStock
                    )
                }
            }
        }
    }
}