package com.summer.notifai.ui.datamodel

data class SearchSmsMessageDataModel(
    val id: Long,
    val senderAddressId: Long,
    val senderAddress: String,
    val message: String,
    val dateInEpoch: Long,
    val date: String,
    val isIncoming: Boolean,
    val smsClassificationDataModel: SmsClassificationDataModel
)
