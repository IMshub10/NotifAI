package com.summer.core.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.summer.core.data.local.entities.SenderType
import com.summer.core.ui.model.SmsImportanceType

data class ContactInfoInboxModel(
    @ColumnInfo("sender_name")
    val senderName: String,
    @ColumnInfo("sender_address_id")
    val senderAddressId: Long,
    @ColumnInfo("phone_number")
    val phoneNumber: String?,
    @ColumnInfo("unread_count")
    val unreadCount: Int,
    @ColumnInfo("sender_type")
    val senderType: SenderType?
) {
    @Ignore
    var smsImportanceType: SmsImportanceType? = null
}