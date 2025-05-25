package com.summer.core.domain.usecase

import com.summer.core.data.local.entities.SmsClassificationTypeEntity
import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class GetSmsTypesUseCase @Inject constructor(
    private val repository: ISmsRepository
) {
    suspend operator fun invoke(): List<SmsClassificationTypeEntity> {
        return repository.getAllSmsClassificationTypes()
    }
}