package com.summer.core.repository

import com.summer.core.data.domain.model.FetchResult
import kotlinx.coroutines.flow.Flow

interface ISmsRepository {
    suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult>
    fun setSmsProcessingStatusCompleted(isCompleted: Boolean)
}