package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import com.example.salestracker.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDropdown(
    selectedProduct: String,
    selectedProductChanged: (Product) -> Unit,
    productOptions: List<Product>
) {
    var expanded by remember { mutableStateOf(false) }

//    val productOptions: List<ProductItem> = emptyList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedProduct,
            onValueChange = {},
            label = { Text("Product") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
//                .menuAnchor()
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)   //replace the above menuAnchor with this better version
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            productOptions.forEach { product ->
                DropdownMenuItem(
                    text = {
                        Text(
                            if (product.inStock) product.name else "${product.name} (out of stock)",
                            color = if (product.inStock) {
                                MaterialTheme.colorScheme.onSurface  // Normal text color
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant  // Greyed out (perfect M3 grey!)
                            },
                            style = MaterialTheme.typography.bodyLarge //Todo added by Grok
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