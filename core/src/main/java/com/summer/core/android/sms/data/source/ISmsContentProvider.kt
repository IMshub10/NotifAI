package com.summer.core.android.sms.data.source

import android.database.Cursor

interface ISmsContentProvider {
    suspend fun getTotalSmsCount(): Int

    suspend fun getLastAndroidSmsId(): Int?

    suspend fun getSmsCursorBetweenIds(fromExclusive: Int, toInclusive: Int, limit: Int): Cursor?

    suspend fun getSmsCursorPreviousIdWithOffset(previousId: Int, limit: Int, offset: Int): Cursor?

    suspend fun getFirstAndroidSmsId(): Int?
}