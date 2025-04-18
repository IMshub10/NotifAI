package com.summer.core.domain.usecase

import androidx.paging.PagingSource
import com.summer.core.data.local.model.SearchSmsMessageQueryModel
import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class SearchMessagesPagingUseCase @Inject constructor(private val repository: ISmsRepository) {
     operator fun invoke(query: String): PagingSource<Int, SearchSmsMessageQueryModel> {
        return repository.getSearchMessagesPagingSource(query)
    }
}