package com.summer.core.domain.usecase

import com.summer.core.data.local.model.SearchSmsMessageQueryModel
import com.summer.core.domain.model.SearchSectionResult
import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class SearchMessagesUseCase @Inject constructor(private val repository: ISmsRepository) {
    suspend operator fun invoke(query: String): SearchSectionResult<SearchSmsMessageQueryModel> {
        return repository.searchMessages(query)
    }
}