package com.summer.core.ui

enum class SmsImportanceType(val value: Int) {
    IMPORTANT(1),
    UNIMPORTANT(0),
    ALL(-1);

    companion object {
        fun fromValue(value: Int): SmsImportanceType? {
            return entries.find { it.value == value }
        }

        fun Boolean?.toSmsImportanceType(): SmsImportanceType = when (this) {
            true -> IMPORTANT
            false -> UNIMPORTANT
            null -> ALL
        }
    }
}