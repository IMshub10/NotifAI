package com.summer.core.android.sms.mapper

import android.content.ContentValues
import android.provider.Telephony
import com.summer.core.android.sms.data.SmsInfoModel
import com.summer.core.android.sms.util.SmsMessageType

object SmsContentValuesBuilder {

    fun build(sms: SmsInfoModel, threadId: Long?): ContentValues {
        return ContentValues().apply {
            put(Telephony.Sms.ADDRESS, sms.address)
            put(Telephony.Sms.BODY, sms.body)
            put(Telephony.Sms.DATE, System.currentTimeMillis())
            put(Telephony.Sms.DATE_SENT, sms.timestamp)
            put(Telephony.Sms.READ, 0)
            put(Telephony.Sms.TYPE, SmsMessageType.INBOX.code)
            put(Telephony.Sms.STATUS, -1)
            put(Telephony.Sms.LOCKED, 0)
            put(Telephony.Sms.SERVICE_CENTER, sms.serviceCenter)
            sms.subscriptionId?.let {
                put(Telephony.Sms.SUBSCRIPTION_ID, it)
            }
            threadId?.let { put(Telephony.Sms.THREAD_ID, it) }
        }
    }
}