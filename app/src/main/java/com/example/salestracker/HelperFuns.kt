package com.example.salestracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateForDisplay(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy") // e.g., 5-July-2025
    return try {
        val date = LocalDate.parse(dateString, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        dateString // fallback in case of parsing error
    }
}

/**
 * Extension function to format a Double into a currency-style string (2 decimal places).
 * Usage: myDouble.toCurrencyString()
 */
fun Double.toCurrencyString(): String {
    return String.format(Locale.getDefault(), "%.2f", this)
}
