package com.example.salestracker.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Converts a database date string (YYYY-MM-DD) to a pretty display string (YYYY-MMM-DD).
     * Example: "2026-06-05" -> "2026-Jun-05"
     */
    fun formatForDisplay(isoDate: String): String {
        if (isoDate.isBlank()) return isoDate
        return try {
            val date = LocalDate.parse(isoDate, isoFormatter)
            // We use the pattern directly here to ensure the formatter always uses the
            // CURRENT system locale, which satisfies the Android lint check.
            val displayFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd", Locale.getDefault())
            date.format(displayFormatter)
        } catch (e: Exception) {
            isoDate // Fallback if format is unexpected
        }
    }
}