package com.example.salestracker.ui.screens.sale.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.salestracker.data.model.Product
import com.example.salestracker.ui.components.ProductDropdown

@Composable
fun SaleItemCard(
    itemState: SaleItemState,
    productOptions: List<Product>,
    onAction: (AddSaleAction) -> Unit,
    modifier: Modifier = Modifier,
    cardNumber: Int = 0
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item $cardNumber Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { onAction(AddSaleAction.RemoveSaleItem(itemState.saleItemId)) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Product Selection
            ProductDropdown(
                selectedProduct = itemState.product.name,
                productOptions = productOptions,
                selectedProductChanged = { product ->
                    onAction(
                        AddSaleAction.SaleEntryAction(
                            itemState.saleItemId,
                            SaleItemAction.ProductChanged(product)
                        )
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Price Field
                OutlinedTextField(
                    value = if (itemState.salePrice == 0.0) "" else itemState.salePrice.toString(),
                    onValueChange = { newPrice ->
                        onAction(
                            AddSaleAction.SaleEntryAction(
                                itemState.saleItemId,
                                SaleItemAction.ProductPriceChanged(newPrice)
                            )
                        )
                    },
                    label = { Text("Price") },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                // Quantity Field
                OutlinedTextField(
                    value = itemState.quantity?.toString() ?: " 1",
                    onValueChange = { newQty ->
                        onAction(
                            AddSaleAction.SaleEntryAction(
                                itemState.saleItemId,
                                SaleItemAction.QuantityChanged(newQty)
                            )
                        )
                    },
                    label = { Text("Qty") },
                    modifier = Modifier.width(100.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}