package com.summer.core.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats an epoch timestamp (in milliseconds) into a user-friendly date/time string.
 *
 * If the input time corresponds to the current day, it returns the time in "hh:mm a" format (e.g., "08:30 AM").
 * Otherwise, it returns the date in two formats separated by a newline:
 * - "dd MMMM" (e.g., "01 April")
 * - "dd-MM-yy" (e.g., "01-04-25")
 *
 * @param timeInMillis The epoch time in milliseconds to format.
 * @return A formatted string representing the time or date, depending on whether it is today.
 *
 * @sample
 * val formatted = formatEpoch(1711966200000L)
 * println(formatted) // Output: "08:30 AM" (if today) or "01 April\n01-04-25"
 */
fun formatEpoch(timeInMillis: Long): String {
    val inputDate = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
    val currentDate = Calendar.getInstance()

    return if (inputDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
        inputDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
    ) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timeInMillis))
    } else {
        val dayMonthFormat = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(Date(timeInMillis))
        val fullDateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(Date(timeInMillis))
        "$dayMonthFormat\n$fullDateFormat"
    }
}