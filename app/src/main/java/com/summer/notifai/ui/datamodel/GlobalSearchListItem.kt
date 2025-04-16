package com.summer.notifai.ui.datamodel

import com.summer.core.domain.model.SearchSectionId

sealed class GlobalSearchListItem {
    data class SectionHeader(val id: SearchSectionId, val titleResId: Int, val count: Int) :
        GlobalSearchListItem()

    data class SmsItem(val data: SearchSmsMessageDataModel) : GlobalSearchListItem()
    data class ConversationItem(val data: ContactMessageInfoDataModel) : GlobalSearchListItem()
    data class ContactItem(val data: NewContactDataModel) : GlobalSearchListItem()
}