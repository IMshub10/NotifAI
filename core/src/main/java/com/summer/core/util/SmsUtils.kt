package com.summer.core.util

import com.summer.core.data.local.entities.SenderType

fun String.determineSenderType(): SenderType {
    return when {
        matches(Regex("^\\+?[0-9]{8,15}$")) -> SenderType.CONTACT

        else -> SenderType.BUSINESS
    }
}

fun String.trimSenderId(): String {
    return replace(Regex("^[A-Z]{2}-"), "")
}

fun String.normalizePhoneNumber(defaultCountryCode: Int): String {
    return when {
        matches(Regex("^\\+[0-9]{10,15}$")) -> this

        matches(Regex("^91[0-9]{10}$")) -> "+$this"

        matches(Regex("^0[0-9]{10}$")) -> "+$defaultCountryCode${substring(1)}"

        matches(Regex("^[0-9]{10}$")) -> "+$defaultCountryCode$this"

        matches(Regex("^[789][0-9]{9}$")) -> "$defaultCountryCode$this"

        else -> this
    }
}