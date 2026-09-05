package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StockStatusRow(
    inStock: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "IN STOCK",
            style = MaterialTheme.typography.labelLarge,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 8.dp)
        )

        Checkbox(
            checked = inStock,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(2f).padding(start = 8.dp)
        )
    }
}

@Preview
@Composable
fun StockPreview() {
    StockStatusRow(
        inStock = true,
        onCheckedChange = {  }
    )
}