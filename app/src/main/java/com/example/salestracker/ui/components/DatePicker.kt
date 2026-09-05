package com.example.salestracker.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.salestracker.utils.DateUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    autoFillToday: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Autofill current date if blank and enabled
    LaunchedEffect(Unit) {
        if (value.isBlank() && autoFillToday) {
            onValueChange(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
    }

    // Trigger dialog on click
    LaunchedEffect(isPressed) {
        if (isPressed) {
            showDialog = true
        }
    }

    val initialDate = try {
        LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        LocalDate.now()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )

    val displayValue = remember(value) {
        DateUtils.formatForDisplay(value)
    }

    OutlinedTextField(
        value = displayValue,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        modifier = modifier.fillMaxWidth(),
        interactionSource = interactionSource,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
            }
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onValueChange(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}