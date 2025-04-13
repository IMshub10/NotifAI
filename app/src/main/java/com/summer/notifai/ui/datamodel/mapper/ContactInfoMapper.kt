package com.summer.notifai.ui.datamodel.mapper

import com.summer.core.data.local.model.ContactMessageInfoModel
import com.summer.core.util.DateUtils

import com.summer.notifai.R
import com.summer.notifai.ui.datamodel.ContactMessageInfoDataModel

object ContactInfoMapper {
    fun ContactMessageInfoModel.toContactMessageInfoDataModel(): ContactMessageInfoDataModel {
        return ContactMessageInfoDataModel(
            icon = R.drawable.ic_sms_24x24,
            senderAddressId = senderAddressId,
            senderName = senderName,
            rawAddress = rawAddress,
            lastMessage = lastMessage,
            lastMessageDate = DateUtils.formatDisplayDateTime(lastMessageDate),
            unreadCount = unreadCount.takeIf { it != 0 }?.toString(),
            senderType = senderType
        )
    }
}