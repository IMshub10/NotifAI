package com.summer.core.data.repository

import android.content.ContentValues
import android.content.Context
import android.provider.Telephony
import android.util.Log
import androidx.paging.PagingSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.device.util.DeviceTierEvaluator
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.data.mapper.SmsMapper
import com.summer.core.android.sms.data.model.SmsInfoModel
import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.android.sms.util.SmsStatus
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.entities.SmsEntity
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.data.local.preference.PreferenceKey
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.domain.model.FetchResult
import com.summer.core.domain.repository.ISmsRepository
import com.summer.core.ui.model.SmsImportanceType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    private val smsDao: SmsDao,
    private val smsBatchProcessor: SmsBatchProcessor,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val deviceTierEvaluator: DeviceTierEvaluator
) : ISmsRepository {

    override suspend fun fetchSmsMessagesFromDevice(): Flow<FetchResult> {
        val batchSettings = deviceTierEvaluator.getRecommendedBatchSettings()
        return smsBatchProcessor.processSmsInBatches(
            batchSize = batchSettings.first,
            batchSettings.second
        )
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

    override suspend fun insertSms(smsEntity: SmsEntity): Long {
        return smsDao.insertSmsMessage(smsEntity)
    }

    override suspend fun markSmsAsReadBySenderId(
        context: Context,
        senderAddressId: Long
    ): List<Long> {
        val smsIds = smsDao.getUnreadAndroidSmsIdsBySenderAddressId(senderAddressId)
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
        return smsIds
    }

    override suspend fun markSmsAsSentStatus(
        context: Context,
        smsId: Long,
        status: SmsStatus
    ): Long? {
        return try {
            val smsEntity = smsDao.getSmsEntityById(smsId) ?: return null

            // Already inserted → skip system insert
            if (smsEntity.androidSmsId != null) {
                // Still update status as SENT
                smsDao.updateSmsStatusById(smsId, status.code, System.currentTimeMillis())
                return smsEntity.androidSmsId.toLong()
            }

            val values = SmsMapper.smsEntityToContentValuesForSent(smsEntity)

            val uri = context.contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values)
            val androidSmsId = uri?.lastPathSegment?.toLongOrNull()

            if (androidSmsId != null) {
                smsDao.updateAndroidSmsId(smsEntity.id, androidSmsId.toInt())
                smsDao.updateSmsStatusById(smsEntity.id, status.code, System.currentTimeMillis())
            }

            androidSmsId
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("SmsRepository", "Failed to insert sent SMS into system provider", e)
            null
        }
    }

    override suspend fun markSmsAsDeliveredStatus(
        context: Context,
        smsId: Long,
        status: SmsStatus
    ): Boolean {
        return try {
            val androidSmsId = smsDao.getAndroidSmsIdById(smsId)

            if (androidSmsId != null) {
                // Case 1: Already inserted in system provider → just update status
                val values = ContentValues().apply {
                    put(Telephony.Sms.STATUS, status.code)
                }

                val updatedRows = context.contentResolver.update(
                    Telephony.Sms.CONTENT_URI,
                    values,
                    "${Telephony.Sms._ID} = ?",
                    arrayOf(androidSmsId.toString())
                )

                smsDao.updateSmsStatusById(smsId, status.code, System.currentTimeMillis())
                return updatedRows > 0
            } else {
                // Case 2: Not inserted yet → insert, update androidSmsId + status

                val smsEntity = smsDao.getSmsEntityById(smsId) ?: return false
                val values = SmsMapper.smsEntityToContentValuesForSent(smsEntity)

                val uri = context.contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, values)
                val insertedAndroidSmsId = uri?.lastPathSegment?.toLongOrNull()

                if (insertedAndroidSmsId != null) {
                    smsDao.updateAndroidSmsId(smsEntity.id, insertedAndroidSmsId.toInt())
                    smsDao.updateSmsStatusById(
                        smsEntity.id,
                        status.code,
                        System.currentTimeMillis()
                    )
                    return true
                }
            }

            false
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("SmsRepository", "Failed to mark SMS as delivered (id=$smsId)", e)
            false
        }
    }
}