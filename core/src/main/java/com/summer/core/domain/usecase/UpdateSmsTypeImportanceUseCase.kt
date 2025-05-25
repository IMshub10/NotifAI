package com.summer.core.domain.usecase

import com.summer.core.domain.repository.ISmsRepository
import javax.inject.Inject

class UpdateSmsTypeImportanceUseCase @Inject constructor(
    private val repository: ISmsRepository
) {
    suspend operator fun invoke(id: Int, isImportant: Boolean) {
        repository.updateSmsTypeImportance(id, isImportant)
    }
}