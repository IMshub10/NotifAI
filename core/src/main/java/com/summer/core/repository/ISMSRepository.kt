package com.summer.core.repository

import com.summer.core.data.domain.model.FetchResult
import kotlinx.coroutines.flow.Flow

interface ISMSRepository {
    suspend fun fetchSMSFromDevice(): Flow<FetchResult>
    fun setSMSProcessingStatusCompleted(isCompleted: Boolean)
}