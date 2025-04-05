package com.summer.core.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.summer.core.ui.SmsImportanceType

data class ContactInfoInboxModel(
    @ColumnInfo("sender_name")
    val senderName: String,
    @ColumnInfo("sender_address_id")
    val senderAddressId: Long,
    @ColumnInfo("unread_count")
    val unreadCount: Int
) {
    @Ignore
    var smsImportanceType: SmsImportanceType? = null
}