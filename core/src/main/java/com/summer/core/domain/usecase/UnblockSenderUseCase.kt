package com.summer.core.domain.usecase

import com.summer.core.domain.repository.IContactRepository
import javax.inject.Inject

class UnblockSenderUseCase @Inject constructor(
    private val repository: IContactRepository
) {
    suspend operator fun invoke(senderAddressId: Long) {
        repository.unblockSender(senderAddressId)
    }
}