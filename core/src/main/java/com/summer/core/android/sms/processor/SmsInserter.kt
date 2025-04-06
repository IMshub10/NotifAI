package com.summer.core.android.sms.processor

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.util.SmsUtils
import com.summer.core.android.sms.data.mapper.SmsMapper
import com.summer.core.data.local.dao.ContactDao
import com.summer.core.domain.repository.ISmsRepository
import com.summer.core.util.CountryCodeProvider
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.entities.SmsEntity
import com.summer.core.data.local.preference.PreferenceKey
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.ml.model.SmsClassifierModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles incoming SMS processing.
 * Based on feature flag, inserts SMS into:
 * 1. Android system inbox (content provider)
 * 2. App's internal Room database
 */
@Singleton
class SmsInserter @Inject constructor(
    private val repository: ISmsRepository,
    private val smsDao: SmsDao,
    private val contactDao: ContactDao,
    private val countryCodeProvider: CountryCodeProvider,
    private val preferencesManager: SharedPreferencesManager,
    private val smsClassifierModel: SmsClassifierModel
) {
    /**
     * Handles incoming SMS from the BroadcastReceiver.
     * @param context Context used for content resolver queries
     * @param intent The SMS_RECEIVED broadcast intent
     */
    suspend fun processIncomingSms(context: Context, intent: Intent): SmsEntity? {
        val smsEnt = SmsUtils.extractSmsFromIntent(intent)
        val defaultCountryCode = countryCodeProvider.getMyCountryCode()
        val dontShareSmsData =
            preferencesManager.getDataBoolean(PreferenceKey.DONT_SHARE_SMS_DATA, false)

        var smsEntity: SmsEntity? = null

        smsEnt?.let { sms ->
            val threadId = sms.address?.let { SmsUtils.getOrCreateThreadId(context, it) }

            val insertedId = if (!dontShareSmsData) {
                repository.insertSms(context, sms, threadId)
            } else {
                null
            }

            if (insertedId != null) {
                val cursor = context.contentResolver.query(
                    Telephony.Sms.CONTENT_URI,
                    SmsMapper.projection,
                    "${SMSColumnNames.COLUMN_ID} = ?",
                    arrayOf(insertedId.toString()),
                    null
                )
                SmsMapper.mapCursorToSms(cursor, smsDao, defaultCountryCode)?.let {
                    smsEntity = it
                }
            }
            if (smsEntity == null) {
                smsEntity = SmsMapper.smsInfoModelToSmsEntity(sms, smsDao, defaultCountryCode)
            }
        }

        // Insert all collected messages into the local DB at once
        smsEntity?.let {
            val classifiedSms = classifySms(it)
            val long = smsDao.insertSmsMessage(classifiedSms)
            smsEntity = classifiedSms.copy(id = long).apply {
                senderName = contactDao.getSenderNameById(senderAddressId)
            }
        }
        return smsEntity
    }

    private suspend fun classifySms(sms: SmsEntity): SmsEntity {
        return try {
            val classification = withContext(Dispatchers.Default) { // Run classification on Default
                smsClassifierModel.classifySms(sms.rawAddress, sms.body)
            }
            sms.copy(
                importanceScore = classification.importanceScore,
                smsClassificationTypeId = classification.smsClassificationTypeId,
                confidenceScore = classification.confidenceScore
            )
        } catch (e: Exception) {
            Log.w(
                "classifySms",
                "Error classifying SMS with address: ${sms.rawAddress}, body: ${sms.body}",
                e
            )
            FirebaseCrashlytics.getInstance().recordException(e)
            sms // Return original SMS on classification failure
        }
    }
}