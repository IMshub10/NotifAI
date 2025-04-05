package com.summer.core.util

import android.text.format.DateUtils as AndroidDateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val FORMAT_TIME_12_HOUR = "hh:mm a"
    private const val FORMAT_DAY_MONTH = "dd MMMM"
    private const val FORMAT_FULL_DATE = "dd-MM-yy"
    private const val FORMAT_DAY_HEADER_SAME_YEAR = "EEEE, d MMMM"
    private const val FORMAT_DAY_HEADER_DIFF_YEAR = "EEEE, d MMMM yyyy"

    /**
     * Formats an epoch timestamp into a user-friendly string:
     * - If it's today → returns "hh:mm a"
     * - Else → returns "dd MMMM\n dd-MM-yy"
     *
     * @param timeInMillis Epoch time in milliseconds
     * @return Formatted string based on whether it's today
     */
    fun formatDisplayDateTime(timeInMillis: Long): String {
        val inputDate = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
        val currentDate = Calendar.getInstance()

        return if (inputDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
            inputDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
        ) {
            SimpleDateFormat(FORMAT_TIME_12_HOUR, Locale.getDefault()).format(Date(timeInMillis))
        } else {
            val dayMonth = SimpleDateFormat(FORMAT_DAY_MONTH, Locale.getDefault()).format(Date(timeInMillis))
            val fullDate = SimpleDateFormat(FORMAT_FULL_DATE, Locale.getDefault()).format(Date(timeInMillis))
            "$dayMonth\n$fullDate"
        }
    }

    /**
     * Converts epoch time to "hh:mm a" (e.g., "08:30 AM") in device locale.
     *
     * @param timeInMillis Epoch time
     * @return Time string
     */
    fun formatTimeOnly(timeInMillis: Long): String {
        return SimpleDateFormat(FORMAT_TIME_12_HOUR, Locale.getDefault()).format(Date(timeInMillis))
    }

    /**
     * Returns a localized day header string for grouping:
     * - "Today", "Yesterday" if applicable
     * - Else → "Tuesday, 2 April" or "Tuesday, 2 April 2023"
     *
     * @param dateInEpoch Epoch millis
     * @return Formatted label string
     */
    fun formatDayHeader(dateInEpoch: Long): String {
        val now = System.currentTimeMillis()

        val isToday = AndroidDateUtils.isToday(dateInEpoch)
        val isYesterday = AndroidDateUtils.isToday(dateInEpoch + AndroidDateUtils.DAY_IN_MILLIS)

        return when {
            isToday || isYesterday -> AndroidDateUtils.getRelativeTimeSpanString(
                dateInEpoch, now, AndroidDateUtils.DAY_IN_MILLIS, AndroidDateUtils.FORMAT_ABBREV_RELATIVE
            ).toString()

            else -> {
                val smsCal = Calendar.getInstance().apply { timeInMillis = dateInEpoch }
                val thisYear = Calendar.getInstance().get(Calendar.YEAR) == smsCal.get(Calendar.YEAR)
                val pattern = if (thisYear) FORMAT_DAY_HEADER_SAME_YEAR else FORMAT_DAY_HEADER_DIFF_YEAR
                SimpleDateFormat(pattern, Locale.getDefault()).format(Date(dateInEpoch))
            }
        }
    }
}