package com.summer.core.data.repository

import android.content.ContentValues
import android.content.Context
import android.provider.Telephony
import android.util.Log
import androidx.paging.PagingSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.sms.constants.Constants.BATCH_SIZE
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.data.mapper.SmsMapper
import com.summer.core.android.sms.data.model.SmsInfoModel
import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.domain.model.FetchResult
import com.summer.core.domain.repository.ISmsRepository
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.data.local.preference.PreferenceKey
import com.summer.core.data.local.preference.SharedPreferencesManager
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
        sharedPreferencesManager.saveData(PreferenceKey.SMS_PROCESSING_STATUS, isCompleted)
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

    override suspend fun insertSms(context: Context, sms: SmsInfoModel, threadId: Long?): Long? {
        val values = SmsMapper.smsInfoModelToContentValues(sms, threadId)
        return try {
            val uri = context.contentResolver.insert(Telephony.Sms.Inbox.CONTENT_URI, values)
            uri?.lastPathSegment?.toLongOrNull()
        } catch (e: Exception) {
            Log.e("SmsRepository", "Failed to insert SMS", e)
            null
        }
    }

    override suspend fun markSmsAsReadBySenderId(context: Context, senderAddressId: Long) {
        val smsIds = smsDao.getAndroidSmsIdsBySenderAddressId(senderAddressId)
        if (smsIds.isNotEmpty()) {
            try {
                val values = ContentValues().apply {
                    put(Telephony.Sms.READ, 1)
                }

                // Create WHERE clause: "_id IN (?, ?, ?, ...)"
                val whereClause = buildString {
                    append("${SMSColumnNames.COLUMN_ID} IN (")
                    append(smsIds.joinToString(",") { "?" })
                    append(")")
                }

                val whereArgs = smsIds.map { it.toString() }.toTypedArray()

                context.contentResolver.update(
                    Telephony.Sms.CONTENT_URI,
                    values,
                    whereClause,
                    whereArgs
                )
                //mark in room as read
                smsDao.markSmsAsReadBySenderAddressId(senderAddressId)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Log.e("SmsUtils", "Failed to mark SMS list as read", e)
            }
        }
    }
}