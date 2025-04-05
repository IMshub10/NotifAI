package com.summer.notifai.ui.datamodel

data class SmsMessageDataModel(
    val id: Long,
    val message: String,
    val dateInEpoch: Long,
    val date: String,
    val isIncoming: Boolean,
    val smsClassificationDataModel: SmsClassificationDataModel
)