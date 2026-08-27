package com.example.salestracker

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salestracker.data.model.SaleWithItems

@Composable
fun SaleItem(
    modifier: Modifier = Modifier,
    saleWithItems: SaleWithItems,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.Magenta)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = saleWithItems.sale.buyer,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier
                )
                Spacer(Modifier.width(40.dp))
                Text(
                    text = saleWithItems.sale.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
            }
            Text(
                text = saleWithItems.product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp, end = 12.dp)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = saleWithItems.sale.quantity.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total: $${saleWithItems.sale.totalSalePrice}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Text(
                text = saleWithItems.sale.saleNotes ?: "No Sale Notes",
                fontSize = 20.sp,
                fontWeight = FontWeight.W900
            )
        }
    }
}

/*@Preview
@Composable
fun SaleItemPreview() {

    Column(
        Modifier.fillMaxSize()
            .background(color = Color.DarkGray)
    ) {
        SaleItem(
            totalSalePrice = 1200.0,
            saleNotes = "Paid by bank transfer. Moto for Estela",
            products = listOf(
                Product.MetallicEpoxy,
                Product.CounterTopEpoxy,
                Product.NormalEpoxy,
                Product.OneHundredSolids
            )
        )
    }
}*/