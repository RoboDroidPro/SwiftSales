package com.example.salestracker
/*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salestracker.ui.components.ProductDropdown

@Composable
fun AddEditSale(
    modifier: Modifier = Modifier,
//    viewModel: AddEditSaleVM,
    onNavigateToAllSales: () -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        item {
            OutlinedTextField(
                value = "Date",
                onValueChange = { */
/*viewModel.changeDate()*//*
 }, //this was commented out because if date is changeable, we need to parse it before storing it.
                label = { Text("Date") },
                readOnly = true, //Todo this needs to be false or deleted if a way of changing the date is implemented
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
        // OutlinedTextField(value = viewModel.product.value, onValueChange = { viewModel.product.value = it }, label = { Text("Product") }, modifier = Modifier.fillMaxWidth())
        item {
            OutlinedTextField(
                value = "Buyer",
                onValueChange = {  },
                label = { Text("Buyer") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            ProductDropdown(
                selectedProductName = Product.MetallicEpoxy.displayName,
                selectedProductChanged = {  }
            )
        }  //PRODUCT FIELD
        item {
            OutlinedTextField(
                value = "quantity",
                onValueChange = {  },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                value = "TotalSalePrice",
                onValueChange = {  },
                label = { Text("Total Sale Price") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        item {
            OutlinedTextField(
                value = "notes",
                onValueChange = {  },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "Buyer Field Required. Product field required. Price must be a number with" +
                        "up to 2 decimals",
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToAllSales, modifier = Modifier.fillMaxWidth()) {
                Text("View All Sales")
            }
        }
    }
}

@Preview
@Composable
fun TestPreview() {
    Column(modifier = Modifier
        .fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ){
//        Test()
        AddEditSale{ }
    }
}
*/
