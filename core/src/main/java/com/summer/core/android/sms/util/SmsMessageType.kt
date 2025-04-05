package com.summer.core.android.sms.util

enum class SmsMessageType(val code: Int) {
    INBOX(1),
    SENT(2),
    DRAFT(3),
    OUTBOX(4),
    FAILED(5),
    QUEUED(6);

    companion object {
        fun fromCode(code: Int): SmsMessageType? {
            return entries.find { it.code == code }
        }

        fun isIncoming(type: Int): Boolean {
            return type == INBOX.code
        }

        fun isOutgoing(type: Int): Boolean {
            return type in listOf(SENT.code, OUTBOX.code, QUEUED.code)
        }
    }
}