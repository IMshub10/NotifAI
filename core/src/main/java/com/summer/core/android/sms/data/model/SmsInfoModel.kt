package com.summer.core.android.sms.data.model

data class SmsInfoModel(
    val address: String?,
    val body: String,
    val timestamp: Long,
    val serviceCenter: String? = null,
    val subscriptionId: Int? = null
)