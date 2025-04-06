package com.summer.core.domain.repository

import android.content.Context
import androidx.paging.PagingSource
import com.summer.core.android.sms.data.model.SmsInfoModel
import com.summer.core.domain.model.FetchResult
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.ui.SmsImportanceType
import kotlinx.coroutines.flow.Flow

interface ISmsRepository {

    suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult>

    fun setSmsProcessingStatusCompleted(isCompleted: Boolean)

    fun getPagedSmsMessagesPagedBySenderAddressId(
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ): PagingSource<Int, SmsMessageModel>

    suspend fun insertSms(context: Context, sms: SmsInfoModel, threadId: Long?): Long?

    suspend fun markSmsAsReadBySenderId(context: Context, senderAddressId: Long): List<Long>
}