package com.summer.core.android.sms.data.source

import android.database.Cursor

interface ISmsContentProvider {
    fun getSmsCursor(
        limit: Int,
        offset: Int,
        lastFetchedDate: Long?,
        lastFetchedId: Int?
    ): Cursor?

    fun getTotalSmsCount(): Int
}