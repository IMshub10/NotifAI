package com.summer.core.domain.usecase

import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class IsSenderBlockedUseCase @Inject constructor(
    private val repository: ISmsRepository
) {
    suspend operator fun invoke(senderAddressId: Long): Boolean {
        return repository.isSenderBlocked(senderAddressId)
    }
}