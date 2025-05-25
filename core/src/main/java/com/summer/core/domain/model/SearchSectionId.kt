package com.summer.core.domain.model

enum class SearchSectionId(val id: Int) {
    MESSAGES(1),
    CONVERSATIONS(2),
    CONTACTS(4);

    companion object {
        fun fromId(id: Int): SearchSectionId =
            entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Unknown SearchSectionId id: $id")
    }
}