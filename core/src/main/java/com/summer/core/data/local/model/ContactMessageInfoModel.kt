package com.summer.core.data.local.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import com.summer.core.data.local.entities.SenderType

@Keep
data class ContactMessageInfoModel(
    @ColumnInfo("sender_name")
    val senderName: String,
    @ColumnInfo("sender_address_id")
    val senderAddressId: Long,
    @ColumnInfo("raw_address")
    val rawAddress: String,
    @ColumnInfo("last_message")
    val lastMessage: String,
    @ColumnInfo("last_message_date")
    val lastMessageDate: Long,
    @ColumnInfo("unread_count")
    val unreadCount: Int,
    @ColumnInfo("sender_type")
    val senderType: SenderType?
)