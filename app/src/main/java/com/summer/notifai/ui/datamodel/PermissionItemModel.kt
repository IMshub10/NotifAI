package com.summer.notifai.ui.datamodel

import androidx.annotation.DrawableRes

data class PermissionItemModel(
    @DrawableRes val icon: Int,
    val title: String,
    val description: String,
    val isFirst: Boolean,
    val isLast: Boolean,
)