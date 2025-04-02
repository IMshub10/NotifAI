package com.summer.core.repository

import androidx.paging.PagingData
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.model.ContactMessageInfoModel
import kotlinx.coroutines.flow.Flow

interface ISmsRepository {
    suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult>
    fun setSmsProcessingStatusCompleted(isCompleted: Boolean)
}