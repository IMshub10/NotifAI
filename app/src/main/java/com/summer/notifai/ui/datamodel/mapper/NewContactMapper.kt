package com.summer.notifai.ui.datamodel.mapper

import com.summer.core.android.phone.data.entity.ContactEntity
import com.summer.notifai.ui.datamodel.NewContactDataModel

object NewContactMapper {
    fun ContactEntity.toNewContactDataModel(): NewContactDataModel {
        return NewContactDataModel(
            id = id,
            icon = com.summer.core.R.drawable.ic_contact_24x24,
            contactName = name,
            phoneNumber = phoneNumber
        )
    }
}