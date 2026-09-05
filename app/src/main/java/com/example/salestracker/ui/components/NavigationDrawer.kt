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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SalesNavDrawer(
    onNavToSettings: () -> Unit = {},
    drawerState: DrawerState = DrawerState(DrawerValue.Closed),
    headerTitle: String = "Sales Tracker",
    content: @Composable () -> Unit = {  }
) {

    ModalNavigationDrawer(
        drawerContent = { SalesDrawerSheet(headerTitle = headerTitle, onNavToSettings) },
        drawerState = drawerState,
        content = content
    )
}

@Composable
fun SalesDrawerSheet(
    headerTitle: String = "Sales Tracker",
    onSettingsClick: () -> Unit = {},
    ) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(280.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            DrawerHeader(headerTitle = headerTitle)
            Spacer(Modifier.height(40.dp))
            DrawerNavButton(
                buttonIcon = Icons.Default.Star,
                buttonLabel = "Star",
                buttonIconContentDescription = "Star"
            )
            DrawerNavButton(
                navButtonClick = onSettingsClick,
                buttonIcon = Icons.Default.Settings,
                buttonLabel = "Settings",
                buttonIconContentDescription = "Go to Settings"
            )
        }
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    headerTitle: String = "Sales Master"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        /*Row(
            modifier = modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {},
                modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    Modifier.size(40.dp)
                )
            }
            Spacer(Modifier.width(20.dp))
            Text(
                text = headerTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = modifier//.padding(20.dp)
            )
        }*/
        Text(
            text = headerTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier//.padding(20.dp)
        )
        HorizontalDivider(thickness = 2.dp)
    }
}

/*@Composable
fun Test() {
    Column() {
        ModalNavigationDrawer(
            drawerContent = {  },
            modifier = Modifier,
            drawerState = DrawerState(DrawerValue.Closed),
        ) {
            ModalDrawerSheet(Modifier.width(280.dp)) {
                DrawerHeader()
                SalesDrawerSheet()
            }
        }
    }
}*/

@Composable
fun DrawerNavButton(
    navButtonClick: () -> Unit = {},
    buttonIcon: ImageVector = Icons.Default.Add,
    buttonIconContentDescription: String = "Add",
    buttonLabel: String = "Add",
    iconTintColor: Color = LocalContentColor.current
) {
    TextButton(
        onClick = navButtonClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = buttonIconContentDescription,
                tint = iconTintColor,
                modifier = Modifier
                    .size(40.dp)
            )
            Text(
                text = buttonLabel,
//                style = MaterialTheme.typography.bodyMedium,
//                fontSize = 28.sp,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
                color = iconTintColor,
                modifier = Modifier.padding(start = 16.dp)

            )
        }
    }
}

/*@Preview
@Composable
fun DrawerPreview() {
//    Column(Modifier
//        .background(Color.Red)
//        .fillMaxSize()) {
////        SalesDrawerContent()
////        Test()
        SalesNavDrawer()
//    }
//    DrawerNavButton()

}*/

/*    ModalNavigationDrawer(
        drawerContent = {},
        modifier = modifier
            .width(200.dp)
            .background(color = Color.LightGray),

        drawerState = drawerState,
        content = content
    )*/