package com.summer.core.domain.usecase

import androidx.paging.PagingSource
import com.summer.core.android.phone.data.entity.ContactEntity
import com.summer.core.domain.repository.IContactRepository
import javax.inject.Inject

class GetContactListWithFilterUseCase @Inject constructor(
    private val contactRepository: IContactRepository
) {
    operator fun invoke(
        query: String
    ): PagingSource<Int, ContactEntity> {
        return contactRepository.getContactsWithFilter(query)
    }
}