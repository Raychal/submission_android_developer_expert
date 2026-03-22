package com.raychal.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatGameDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"
    return try {
        val datePart = if (dateString.contains("T")) {
            dateString.substringBefore("T")
        } else {
            dateString
        }

        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())

        val date = LocalDate.parse(datePart, inputFormatter)
        date.format(outputFormatter)
    } catch (_: Exception) {
        dateString
    }
}