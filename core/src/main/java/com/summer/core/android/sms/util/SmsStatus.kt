package com.summer.core.android.sms.util

enum class SmsStatus(val code: Int) {
    NONE(-1),
    PENDING(32),
    FAILED(64),
    SENT(0),              // Message was sent (SMS_SENT success)
    DELIVERED(100),       // Custom status - delivery confirmed
    DELIVERY_FAILED(101); // Custom status - delivery failed

    companion object {
        fun fromCode(code: Int?): SmsStatus {
            return entries.find { it.code == code } ?: NONE
        }
    }
}