package com.summer.core.domain.model

data class SearchSectionResult<T>(
    val header: SearchSectionHeader,
    val items: List<T>
)