package com.example.salestracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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