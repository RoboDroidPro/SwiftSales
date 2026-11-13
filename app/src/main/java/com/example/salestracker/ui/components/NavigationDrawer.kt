package com.example.salestracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salestracker.R

@Composable
fun SalesNavDrawer(
    onNavToSettings: () -> Unit = {},
    onNavToStats: () -> Unit = {},
    onNavToHelp: () -> Unit = {},
    drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    headerTitle: String = "SwiftSales",
    content: @Composable () -> Unit = {}
) {
    ModalNavigationDrawer(
        drawerContent = {
            SalesDrawerSheet(
                headerTitle = headerTitle,
                onSettingsClick = onNavToSettings,
                onStatsClick = onNavToStats,
                onHelpClick = onNavToHelp
            )
        },
        drawerState = drawerState,
        content = content
    )
}

@Composable
fun SalesDrawerSheet(
    headerTitle: String = "SwiftSales",
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            DrawerHeader(headerTitle = headerTitle)
            Spacer(Modifier.height(24.dp))

            DrawerNavButton(
                navButtonClick = onStatsClick,
                buttonIcon = Icons.Default.BarChart,
                buttonLabel = stringResource(R.string.stats_title),
                buttonIconContentDescription = stringResource(R.string.go_to_stats_description)
            )
            DrawerNavButton(
                navButtonClick = onSettingsClick,
                buttonIcon = Icons.Outlined.Category,
                buttonLabel = stringResource(R.string.settings_label),
                buttonIconContentDescription = stringResource(R.string.go_to_settings_description)
            )
            DrawerNavButton(
                navButtonClick = onHelpClick,
                buttonIcon = Icons.AutoMirrored.Outlined.HelpOutline,
                buttonLabel = stringResource(R.string.help_about_title),
                buttonIconContentDescription = stringResource(R.string.go_to_help_description)
            )
        }
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    headerTitle: String = "SwiftSales"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = headerTitle,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        HorizontalDivider(
            thickness = 1.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun DrawerNavButton(
    navButtonClick: () -> Unit = {},
    buttonIcon: ImageVector = Icons.Default.Add,
    buttonIconContentDescription: String = "Add",
    buttonLabel: String = "Add",
    iconTintColor: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = navButtonClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = buttonIconContentDescription,
                tint = iconTintColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = buttonLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
