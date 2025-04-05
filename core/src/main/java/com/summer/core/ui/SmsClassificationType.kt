package com.summer.core.ui

enum class SmsClassificationType(val compactName: String) {
    SCAM("SCAM"),
    PROMOTIONAL("PROMOTIONAL"),
    IMPORTANT("IMPORTANT"),
    TRANSACTION("TRANSACTION"),
    OTP("OTP"),
    ALERT("ALERT");

    companion object {
        fun fromCompactName(name: String?): SmsClassificationType? {
            return entries.find { it.compactName.equals(name, ignoreCase = true) }
        }
    }
}