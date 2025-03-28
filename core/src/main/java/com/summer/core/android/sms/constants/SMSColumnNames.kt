package com.summer.core.android.sms.constants

import android.provider.Telephony

object SMSColumnNames {
    const val COLUMN_ID = Telephony.Sms._ID
    const val COLUMN_ADDRESS = Telephony.Sms.ADDRESS
    const val COLUMN_BODY = Telephony.Sms.BODY
    const val COLUMN_DATE = Telephony.Sms.DATE
    const val COLUMN_DATE_SENT = Telephony.Sms.DATE_SENT
    const val COLUMN_TYPE = Telephony.Sms.TYPE
    const val COLUMN_THREAD_ID = Telephony.Sms.THREAD_ID
    const val COLUMN_STATUS = Telephony.Sms.STATUS
    const val COLUMN_SERVICE_CENTER = Telephony.Sms.SERVICE_CENTER
    const val COLUMN_LOCKED = Telephony.Sms.LOCKED
    const val COLUMN_PERSON = Telephony.Sms.PERSON

    const val COLUMN_TOTAL_SMS_COUNT = "total_sms_count"
}