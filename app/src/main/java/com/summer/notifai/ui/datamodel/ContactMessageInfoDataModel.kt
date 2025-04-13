package com.summer.notifai.ui.datamodel

import com.summer.core.data.local.entities.SenderType

data class ContactMessageInfoDataModel(
    val senderAddressId: Long,
    val senderName: String,
    val rawAddress: String,
    val lastMessage: String,
    val lastMessageDate: String,
    val unreadCount: String? = null,
    val senderType: SenderType?
)