package com.summer.core.data.local.model

import androidx.room.ColumnInfo

data class SmsPagingKey(
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("date")
    val date: Long
)