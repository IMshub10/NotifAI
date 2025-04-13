package com.summer.notifai.ui.datamodel

import androidx.annotation.DrawableRes

data class NewContactDataModel(
    val id: Long,
    @DrawableRes val icon: Int,
    val contactName: String,
    val phoneNumber: String,
)