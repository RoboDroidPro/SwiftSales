package com.example.salestracker.utils

/**
 * Converts a decimal currency string into an integer representing cents.
 *
 * Examples:
 * "2.98" -> 298
 * "2.1"  -> 210
 * "20"   -> 2000
 *
 * Returns null if the input is not a valid currency value
 * or contains more than two decimal places.
 */
fun String.toSwiftCurrency(): Int? {
    val parts = this.trim().split(".")

    if (parts.size > 2) return null

    val dollars = parts[0].toIntOrNull() ?: return null

    val cents = when {
        parts.size == 1 -> 0

        parts[1].isEmpty() -> 0

        parts[1].length == 1 -> {
            parts[1].toIntOrNull()?.times(10) ?: return null
        }
        parts[1].length == 2 -> {
            parts[1].toIntOrNull() ?: return null
        }
        else -> return null
    }

    return dollars * 100 + cents
}

/**
 * Converts an integer representing cents into a currency string
 * with exactly two decimal places.
 *
 * Examples:
 * 298  -> "2.98"
 * 210  -> "2.10"
 * 2000 -> "20.00"
 */
fun Int.toSwiftString(): String {
    val dollars = this / 100
    val cents = this % 100

    return "$dollars.${cents.toString().padStart(2, '0')}"
}