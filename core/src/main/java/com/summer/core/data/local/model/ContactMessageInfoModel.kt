package com.summer.core.data.local.model

import androidx.room.ColumnInfo

data class ContactMessageInfoModel(
    @ColumnInfo("sender_name")
    val senderName: String,
    @ColumnInfo("raw_address")
    val rawAddress: String,
    @ColumnInfo("last_message")
    val lastMessage: String,
    @ColumnInfo("last_message_date")
    val lastMessageDate: Long,
    @ColumnInfo("unread_count")
    val unreadCount: Int
)