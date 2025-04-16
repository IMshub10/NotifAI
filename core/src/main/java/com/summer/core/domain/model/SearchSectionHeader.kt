package com.summer.core.domain.model

data class SearchSectionHeader(
    val id: SearchSectionId,
    val titleResId: Int, // e.g. R.string.search_section_messages
    val count: Int
)