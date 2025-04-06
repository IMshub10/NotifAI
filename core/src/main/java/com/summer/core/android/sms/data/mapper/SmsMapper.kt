package com.summer.core.android.sms.data.mapper

import android.content.ContentValues
import android.database.Cursor
import android.provider.Telephony
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.data.model.SmsInfoModel
import com.summer.core.android.sms.util.SmsMessageType
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.entities.SmsEntity

object SmsMapper {

    val projection = arrayOf(
        SMSColumnNames.COLUMN_ID,
        SMSColumnNames.COLUMN_ADDRESS,
        SMSColumnNames.COLUMN_BODY,
        SMSColumnNames.COLUMN_DATE,
        SMSColumnNames.COLUMN_DATE_SENT,
        SMSColumnNames.COLUMN_TYPE,
        SMSColumnNames.COLUMN_THREAD_ID,
        SMSColumnNames.COLUMN_STATUS,
        SMSColumnNames.COLUMN_SERVICE_CENTER,
        SMSColumnNames.COLUMN_LOCKED,
        SMSColumnNames.COLUMN_PERSON,
        SMSColumnNames.COLUMN_READ,
        SMSColumnNames.COLUMN_SUBSCRIPTION_ID,
    )

    fun mapCursorToSmsList(
        cursor: Cursor?,
        smsDao: SmsDao,
        defaultCountryCode: Int
    ): List<SmsEntity> {
        val smsList = mutableListOf<SmsEntity>()

        cursor?.use {
            while (it.moveToNext()) {
                val sms = SmsEntity(
                    androidSmsId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ID)),
                    senderAddressId = smsDao.getOrInsertSenderId(
                        senderAddress = it.getString(
                            it.getColumnIndexOrThrow(
                                SMSColumnNames.COLUMN_ADDRESS
                            )
                        ).orEmpty(),
                        defaultCountryCode = defaultCountryCode
                    ),
                    rawAddress = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ADDRESS))
                        .orEmpty(),
                    body = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_BODY))
                        .orEmpty(),
                    date = it.getLong(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_DATE)),
                    dateSent = it.getLong(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_DATE_SENT)),
                    type = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_TYPE)),
                    threadId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_THREAD_ID)),
                    read = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_READ)),
                    status = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_STATUS)),
                    serviceCenter = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_SERVICE_CENTER)),
                    subscriptionId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_SUBSCRIPTION_ID)),
                    smsClassificationTypeId = null,
                    importanceScore = null,
                    confidenceScore = null,
                    createdAtApp = System.currentTimeMillis(),
                    updatedAtApp = System.currentTimeMillis()
                )
                smsList.add(sms)
            }
        }
        return smsList
    }

    fun mapCursorToSms(
        cursor: Cursor?,
        smsDao: SmsDao,
        defaultCountryCode: Int
    ): SmsEntity? {
        var sms: SmsEntity? = null
        cursor?.use {
            while (it.moveToNext()) {
                sms = SmsEntity(
                    androidSmsId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ID)),
                    senderAddressId = smsDao.getOrInsertSenderId(
                        senderAddress = it.getString(
                            it.getColumnIndexOrThrow(
                                SMSColumnNames.COLUMN_ADDRESS
                            )
                        ).orEmpty(),
                        defaultCountryCode = defaultCountryCode
                    ),
                    rawAddress = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_ADDRESS))
                        .orEmpty(),
                    body = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_BODY))
                        .orEmpty(),
                    date = it.getLong(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_DATE)),
                    dateSent = it.getLong(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_DATE_SENT)),
                    type = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_TYPE)),
                    threadId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_THREAD_ID)),
                    read = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_READ)),
                    status = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_STATUS)),
                    serviceCenter = it.getString(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_SERVICE_CENTER)),
                    subscriptionId = it.getInt(it.getColumnIndexOrThrow(SMSColumnNames.COLUMN_SUBSCRIPTION_ID)),
                    smsClassificationTypeId = null,
                    importanceScore = null,
                    confidenceScore = null,
                    createdAtApp = System.currentTimeMillis(),
                    updatedAtApp = System.currentTimeMillis()
                )
            }
        }
        return sms
    }

    fun smsInfoModelToSmsEntity(
        sms: SmsInfoModel,
        smsDao: SmsDao,
        defaultCountryCode: Int
    ): SmsEntity {
        return SmsEntity(
            id = 0,
            androidSmsId = 0, // Not inserted in system
            senderAddressId = smsDao.getOrInsertSenderId(
                senderAddress = sms.address.orEmpty(),
                defaultCountryCode = defaultCountryCode
            ),
            rawAddress = sms.address.orEmpty(),
            body = sms.body,
            date = sms.timestamp,
            dateSent = sms.timestamp,
            type = 1,
            threadId = 0,
            read = 0,
            status = 0,
            serviceCenter = sms.serviceCenter,
            subscriptionId = sms.subscriptionId ?: -1,
            smsClassificationTypeId = null,
            importanceScore = null,
            confidenceScore = null,
            createdAtApp = System.currentTimeMillis(),
            updatedAtApp = System.currentTimeMillis()
        )
    }

    fun smsInfoModelToContentValues(sms: SmsInfoModel, threadId: Long?): ContentValues {
        return ContentValues().apply {
            put(Telephony.Sms.ADDRESS, sms.address)
            put(Telephony.Sms.BODY, sms.body)
            put(Telephony.Sms.DATE, System.currentTimeMillis())
            put(Telephony.Sms.DATE_SENT, sms.timestamp)
            put(Telephony.Sms.READ, 0)
            put(Telephony.Sms.TYPE, SmsMessageType.INBOX.code)
            put(Telephony.Sms.STATUS, -1)
            put(Telephony.Sms.LOCKED, 0)
            put(Telephony.Sms.SERVICE_CENTER, sms.serviceCenter)
            sms.subscriptionId?.let {
                put(Telephony.Sms.SUBSCRIPTION_ID, it)
            }
            threadId?.let {
                put(Telephony.Sms.THREAD_ID, it)
            }
        }
    }
}