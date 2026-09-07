package com.example.salestracker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.salestracker.R

@Composable
fun DeleteDialog(
    title: String = stringResource(R.string.delete_action),
    contentText: String = stringResource(R.string.delete_product_dialog_content),
    confirmText: String = stringResource(R.string.delete_action),
    onConfirm: () -> Unit,
    cancelText: String = stringResource(R.string.cancel_action),
    onCancel: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(contentText, fontSize = 18.sp) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelText)
            }
        }
    )
}