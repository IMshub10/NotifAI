package com.summer.core.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.model.ContactMessageInfoModel
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.ui.SmsClassificationType
import com.summer.core.ui.SmsImportanceType
import kotlinx.coroutines.flow.Flow

interface ISmsRepository {

    suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult>

    fun setSmsProcessingStatusCompleted(isCompleted: Boolean)

    fun getPagedSmsMessagesPagedBySenderAddressId(
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ): PagingSource<Int, SmsMessageModel>
}