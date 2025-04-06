package com.summer.core.domain.usecase

import androidx.paging.PagingSource
import com.summer.core.data.local.model.ContactMessageInfoModel
import com.summer.core.domain.repository.IContactRepository
import javax.inject.Inject

class GetContactListByImportanceUseCase @Inject constructor(
    private val contactRepository: IContactRepository
) {
    operator fun invoke(
        isImportant: Boolean
    ): PagingSource<Int, ContactMessageInfoModel> {
        return contactRepository.getPagedContactList(isImportant)
    }
}