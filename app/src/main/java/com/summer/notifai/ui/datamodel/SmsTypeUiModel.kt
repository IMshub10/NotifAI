package com.summer.notifai.ui.datamodel

import com.summer.core.data.local.entities.SmsClassificationTypeEntity

sealed class SmsTypeUiModel {
    data class Header(val title: String) : SmsTypeUiModel()
    data class Item(val entity: SmsClassificationTypeEntity) : SmsTypeUiModel()
}