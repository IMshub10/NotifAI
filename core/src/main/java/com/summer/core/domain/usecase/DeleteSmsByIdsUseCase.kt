package com.summer.core.domain.usecase

import android.content.Context
import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class DeleteSmsByIdsUseCase @Inject constructor(
    private val smsRepository: ISmsRepository
) {
    suspend operator fun invoke(
        context: Context,
        smsIds: List<Long>,
        androidSmsIds: List<Long>
    ): Int {
        return smsRepository.deleteSmsListFromDeviceAndLocal(context, smsIds, androidSmsIds)
    }
}