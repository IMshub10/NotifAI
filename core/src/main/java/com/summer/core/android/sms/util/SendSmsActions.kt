package com.summer.core.android.sms.util

enum class SendSmsActions(val value: String) {
    ACTION_SMS_SENT("SMS_SENT"),
    ACTION_SMS_DELIVERED("SMS_DELIVERED"),
    ACTION_SMS_FAILED("SMS_FAILED")
}