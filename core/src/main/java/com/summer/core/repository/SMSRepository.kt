package com.summer.core.repository

import androidx.paging.PagingSource
import com.summer.core.android.sms.constants.Constants.BATCH_SIZE
import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.data.local.preference.PreferenceKeys
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.ui.SmsClassificationType
import com.summer.core.ui.SmsImportanceType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    private val smsDao: SmsDao,
    private val smsBatchProcessor: SmsBatchProcessor,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ISmsRepository {
    override suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult> {
        return smsBatchProcessor.processSmsInBatches(BATCH_SIZE)
    }

    override fun setSmsProcessingStatusCompleted(isCompleted: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.SMS_PROCESSING_STATUS, isCompleted)
    }

    override fun getPagedSmsMessagesPagedBySenderAddressId(
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ): PagingSource<Int, SmsMessageModel> {
        return smsDao.getPagedSmsMessagesPagedBySenderAddressId(
            senderAddressId,
            smsImportanceType.value
        )
    }
}