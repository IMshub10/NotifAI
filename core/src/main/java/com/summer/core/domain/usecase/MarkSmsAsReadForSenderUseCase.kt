package com.summer.core.domain.usecase

import android.content.Context
import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject


class MarkSmsAsReadForSenderUseCase @Inject constructor(
    private val smsRepository: ISmsRepository
) {
    suspend operator fun invoke(
        context: Context,
        senderAddressId: Long,
    ) {
        smsRepository.markSmsAsReadBySenderId(
            context = context,
            senderAddressId = senderAddressId
        )
    }
}