package com.summer.notifai.ui.datamodel

import androidx.annotation.DrawableRes

data class ContactMessageInfoDataModel(
    @DrawableRes val icon: Int,
    val senderName: String,
    val rawAddress: String,
    val lastMessage: String,
    val lastMessageDate: String,
    val unreadCount: String? = null
)