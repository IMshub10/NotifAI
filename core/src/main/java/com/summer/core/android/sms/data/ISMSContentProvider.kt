package com.summer.core.android.sms.data

import android.database.Cursor

interface ISMSContentProvider {
    fun getSMSCursor(
        limit: Int,
        offset: Int,
        lastFetchedDate: Long?,
        lastFetchedId: Int?
    ): Cursor?

    fun getTotalSMSCount(): Int
}