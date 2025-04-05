package com.summer.notifai.ui.datamodel

sealed class SmsInboxListItem {
    data class Header(val header: SmsMessageHeaderModel) : SmsInboxListItem()
    data class Message(val data: SmsMessageDataModel) : SmsInboxListItem()
}