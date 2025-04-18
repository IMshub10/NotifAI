package com.summer.core.domain.usecase

import androidx.paging.PagingSource
import com.summer.core.android.phone.data.entity.ContactEntity
import com.summer.core.domain.repository.IContactRepository
import javax.inject.Inject

class SearchContactsPagingUseCase @Inject constructor(private val repository: IContactRepository) {
    operator fun invoke(query: String): PagingSource<Int, ContactEntity> {
        return repository.getSearchContactsPagingSource(query)
    }
}