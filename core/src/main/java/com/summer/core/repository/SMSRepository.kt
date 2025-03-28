package com.summer.core.repository

import com.summer.core.android.sms.constants.Constants.BATCH_SIZE
import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.preference.PreferenceKeys
import com.summer.core.data.local.preference.SharedPreferencesManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    private val smsBatchProcessor: SmsBatchProcessor,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ISMSRepository {
    override suspend fun fetchSMSFromDevice(): Flow<FetchResult> {
        return smsBatchProcessor.processSMSInBatches(BATCH_SIZE)
    }

    override fun setSMSProcessingStatusCompleted(isCompleted: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.SMS_PROCESSING_STATUS, isCompleted)
    }
}