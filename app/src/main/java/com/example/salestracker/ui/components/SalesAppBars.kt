package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesAppBar(
    title: String = "SwiftSales",
    onNavigationIconClicked: () -> Unit = {},
    navigationIcon: @Composable (() -> Unit) = { IconButton(onClick = onNavigationIconClicked) { Icon(Icons.Default.Menu, contentDescription = "Open Navigation Drawer") } },
    moreTopBarActions: @Composable (RowScope.() -> Unit) = {},
    scrollBehaviour: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
) {
    TopAppBar(
        title = { Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) },
        navigationIcon = navigationIcon,
        actions = moreTopBarActions,
        scrollBehavior = scrollBehaviour,
        colors = colors
    )
}