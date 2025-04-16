package com.summer.core.android.sms.data.source

import android.database.Cursor

interface ISmsContentProvider {
    suspend fun getTotalSmsCount(): Int

    suspend fun getLastAndroidSmsId(): Int?

    suspend fun getSmsCursorWithOffset(
        offsetId: Int,
        limit: Int,
        offset: Int,
        isOrderAscending: Boolean
    ): Cursor?

    suspend fun getFirstAndroidSmsId(): Int?
}