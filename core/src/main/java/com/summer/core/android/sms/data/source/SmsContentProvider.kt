package com.summer.core.android.sms.data.source

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.constants.SMSColumnNames.COLUMN_TOTAL_SMS_COUNT
import com.summer.core.android.sms.data.mapper.SmsMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsContentProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : ISmsContentProvider {

    private val smsUri: Uri = Telephony.Sms.CONTENT_URI

    override fun getSmsCursor(
        limit: Int,
        offset: Int,
        lastFetchedDate: Long?,
        lastFetchedId: Int?
    ): Cursor? {
        val selection = if (lastFetchedDate != null && lastFetchedId != null) {
            "(${SMSColumnNames.COLUMN_DATE} < ? OR (${SMSColumnNames.COLUMN_DATE} = ? AND ${SMSColumnNames.COLUMN_ID} < ?))"
        } else {
            null
        }
        val selectionArgs = lastFetchedDate?.let { date ->
            lastFetchedId?.let { id ->
                arrayOf(date.toString(), date.toString(), id.toString())
            }
        }

        return contentResolver.query(
            smsUri,
            SmsMapper.projection,
            selection,
            selectionArgs,
            "${SMSColumnNames.COLUMN_DATE} DESC, ${SMSColumnNames.COLUMN_ID} DESC LIMIT $limit OFFSET $offset"
        )
    }

    override fun getTotalSmsCount(): Int {
        val cursor: Cursor? = contentResolver.query(
            smsUri,
            arrayOf("COUNT(*) AS $COLUMN_TOTAL_SMS_COUNT"),  // Query to count total SMS
            null,
            null,
            null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(COLUMN_TOTAL_SMS_COUNT)) // Retrieve count value
            } else {
                0
            }
        } ?: 0
    }
}