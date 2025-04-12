package com.summer.core.android.sms.data.source

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.constants.SMSColumnNames.COLUMN_TOTAL_SMS_COUNT
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides access to SMS data from Android's content provider.
 * Includes utilities for batch paging, fetching ID boundaries, and cursor limiting.
 */
@Singleton
class SmsContentProvider @Inject constructor(
    private val contentResolver: ContentResolver
) : ISmsContentProvider {

    private val smsUri: Uri = Telephony.Sms.CONTENT_URI

    /**
     * Returns the total number of SMS messages available on the device.
     */
    override suspend fun getTotalSmsCount(): Int {
        val cursor: Cursor? = contentResolver.query(
            smsUri,
            arrayOf("COUNT(*) AS $COLUMN_TOTAL_SMS_COUNT"),
            null,
            null,
            null
        )

        return cursor?.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndexOrThrow(COLUMN_TOTAL_SMS_COUNT))
            } else {
                0
            }
        } ?: 0
    }

    /**
     * Returns the latest (highest) Android SMS _id.
     */
    override suspend fun getLastAndroidSmsId(): Int? {
        return contentResolver.query(
            smsUri,
            arrayOf(SMSColumnNames.COLUMN_ID),
            null,
            null,
            "${SMSColumnNames.COLUMN_ID} DESC LIMIT 1"
        )?.use {
            if (it.moveToFirst()) it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ID)) else null
        }
    }

    /**
     * Returns a batch of SMS messages between two IDs (exclusive-inclusive), sorted DESC by _id.
     * Applies limit and simulates cursor trimming if needed.
     */
    override suspend fun getSmsCursorBetweenIds(
        fromExclusive: Int,
        toInclusive: Int,
        limit: Int
    ): Cursor? {
        val cursor = contentResolver.query(
            smsUri,
            null,
            "${SMSColumnNames.COLUMN_ID} > ? AND ${SMSColumnNames.COLUMN_ID} <= ?",
            arrayOf(fromExclusive.toString(), toInclusive.toString()),
            "${SMSColumnNames.COLUMN_ID} DESC"
        )

        return cursor?.let {
            if (it.count > limit) {
                val limitedCursor = getLimitedCursor(it, offset = 0, limit = limit)
                it.close()
                return limitedCursor
            }
            it
        }
    }

    /**
     * Returns a paged batch of SMS messages older than the given ID, using DESC order.
     * Simulates paging using offset and limit.
     */
    override suspend fun getSmsCursorPreviousIdWithOffset(
        previousId: Int,
        limit: Int,
        offset: Int
    ): Cursor? {
        val selection = if (previousId != -1) "${SMSColumnNames.COLUMN_ID} < ?" else null
        val selectionArgs = if (previousId != -1) arrayOf(previousId.toString()) else null

        return contentResolver.query(
            smsUri,
            null,
            selection,
            selectionArgs,
            "${SMSColumnNames.COLUMN_ID} DESC"
        )?.let { cursor ->
            if (cursor.count <= offset) {
                cursor.close()
                return null
            }
            val limitedCursor = getLimitedCursor(cursor = cursor, offset = offset, limit = limit)
            cursor.close()
            return limitedCursor
        }
    }

    /**
     * Cursor limiting utility that returns a sub-cursor with offset and limit.
     */
    private fun getLimitedCursor(cursor: Cursor, offset: Int, limit: Int): Cursor {
        val limitedCursor = android.database.MatrixCursor(cursor.columnNames)
        var currentRow = 0
        while (cursor.moveToNext()) {
            if (currentRow > offset + limit) break
            if (currentRow++ < offset) continue

            val row = Array(cursor.columnCount) { idx ->
                when (cursor.getType(idx)) {
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(idx)
                    Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(idx)
                    Cursor.FIELD_TYPE_STRING -> cursor.getString(idx)
                    Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(idx)
                    else -> null
                }
            }
            limitedCursor.addRow(row)
        }
        return limitedCursor
    }

    /**
     * Returns the earliest (lowest) Android SMS _id.
     */
    override suspend fun getFirstAndroidSmsId(): Int? {
        return contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(SMSColumnNames.COLUMN_ID),
            null,
            null,
            "${SMSColumnNames.COLUMN_ID} ASC LIMIT 1"
        )?.use {
            if (it.moveToFirst()) it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ID)) else null
        }
    }
}
