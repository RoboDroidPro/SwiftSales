package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SaleFAB(
    onFABClick: () -> Unit = {},
    icon: ImageVector = Icons.Filled.Add,
    contentDescription: String = "Add New Sale"
) {
    FloatingActionButton(
        onClick = onFABClick,
        Modifier.size(66.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(40.dp)
        )
    }
}

/*
@Preview
@Composable
fun SaleFab() {
    Column(Modifier.fillMaxSize()) {
        SaleFAB()
    }
}*/
