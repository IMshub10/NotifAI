package com.summer.core.domain.usecase

import com.summer.core.domain.repository.IContactRepository
import javax.inject.Inject

class BlockSenderUseCase @Inject constructor(
    private val repository: IContactRepository
) {
    suspend operator fun invoke(senderAddressId: Long) {
        repository.blockSender(senderAddressId)
    }
}