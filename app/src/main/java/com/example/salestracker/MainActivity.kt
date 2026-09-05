package com.example.salestracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.salestracker.ui.navigation.SaleNavGraph
import com.example.salestracker.ui.theme.SalesTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesTrackerTheme {
                SaleNavGraph()
            }
        }
    }
}







@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SalesTrackerTheme {
//        Column(Modifier.padding(16.dp)) {
//            Spacer(modifier.height(24.dp))
//            OutlinedTextField(value = viewModel.date.value, onValueChange = { viewModel.date.value = it /*it refers to the value that changed*/ }, label = { Text("Date") })
//            OutlinedTextField(value = viewModel.product.value, onValueChange = { viewModel.product.value = it }, label = { Text("Product") })
//            OutlinedTextField(value = viewModel.buyer.value, onValueChange = { viewModel.buyer.value = it }, label = { Text("Buyer") })
//            OutlinedTextField(value = viewModel.notes.value, onValueChange = { viewModel.notes.value = it }, label = { Text("Notes (optional)") })
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = {
//                viewModel.saveSale()
//                Toast.makeText(
//                    context,
//                    "${viewModel.product.value} sold to ${viewModel.buyer.value}. Sale saved.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }) {
//                Text("Save Sale")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(onClick = onNavigateToAllSales) {
//                Text("View All Sales")
//            }
//        }

    }
}


/*
@Composable
fun AllSalesScreen(
    viewModel: SaleViewModel,
    modifier: Modifier = Modifier,
    onAddSale: () -> Unit
) {
    val sales by viewModel.allSales.collectAsState(initial = emptyList())
    val selected = viewModel.selectedSales

    BackHandler(enabled = viewModel.selectedSales.isNotEmpty()) {
        viewModel.clearSelection()
    }


    Column(
        modifier = modifier
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = modifier.weight(1f)
        ) {
            item { Text("All Sales", fontSize = 24.sp, fontWeight = FontWeight.Bold) }

            items(sales) { sale ->
                val isSelected = selected.contains(sale)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.clickable { viewModel.toggleSelection(sale) }
                        .combinedClickable(
                            onClick = {
                                if (viewModel.selectedSales.isNotEmpty()) {
                                    viewModel.toggleSelection(sale)
                                }
                                //else: maybe in the future you could sale details
                            },
                            onLongClick = {
                                viewModel.toggleSelection(sale)
                            }
                        )
                        .background(if (isSelected) Color.LightGray else Color.Transparent)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${sale.product} sold to ${sale.buyer} on ${sale.date}",
                        fontSize = 18.sp,
                        modifier = modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    HorizontalDivider()
                }
            }
        }

        if(selected.isNotEmpty()) {
            Button(
                onClick = { viewModel.deleteSelectedSales() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete selected (${selected.size})")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.clearSelection() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Selection")
            }
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = onAddSale,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Sale")
        }
    }
}

 */