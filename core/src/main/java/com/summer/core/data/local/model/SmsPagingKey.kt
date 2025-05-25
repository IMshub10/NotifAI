package com.summer.core.data.local.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo

@Keep
data class SmsPagingKey(
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("date")
    val date: Long
)