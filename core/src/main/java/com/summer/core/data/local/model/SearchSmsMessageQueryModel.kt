package com.summer.core.data.local.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo

@Keep
data class SearchSmsMessageQueryModel(
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "sender_address_id")
    val senderAddressId: Long,
    @ColumnInfo(name = "sender_address")
    val senderAddress: String,
    @ColumnInfo(name = "body")
    val body: String?,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "sms_message_type")
    val smsMessageType: String?,
    @ColumnInfo(name = "compact_type")
    val compactType: String?
)