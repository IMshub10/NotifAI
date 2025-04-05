package com.summer.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.summer.core.data.local.entities.SenderAddressEntity
import com.summer.core.data.local.entities.SenderType
import com.summer.core.data.local.entities.SmsClassificationTypeEntity
import com.summer.core.data.local.entities.SmsEntity
import com.summer.core.data.local.model.SmsMessageModel
import com.summer.core.ui.SmsImportanceType
import com.summer.core.util.determineSenderType
import com.summer.core.util.normalizePhoneNumber
import com.summer.core.util.trimSenderId

@Dao
interface SmsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSmsMessages(smsList: List<SmsEntity>)

    @Query("SELECT * FROM sms_messages ORDER BY date DESC, android_sms_id DESC LIMIT 1")
    suspend fun getLastInsertedSmsMessage(): SmsEntity?

    @Query("SELECT COUNT(*) FROM sms_messages")
    suspend fun getTotalProcessedSmsCount(): Int

    @Query("SELECT id FROM sender_addresses WHERE sender_address = :address LIMIT 1")
    fun getSenderIdByAddress(address: String): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSenderAddress(sender: SenderAddressEntity): Long

    @Transaction
    fun getOrInsertSenderId(senderAddress: String, defaultCountryCode: Int): Long {
        val senderType = senderAddress.determineSenderType()

        val trimmedAddress = when (senderType) {
            SenderType.BUSINESS -> senderAddress.trimSenderId()
            SenderType.CONTACT -> senderAddress.normalizePhoneNumber(defaultCountryCode)
        }.uppercase()

        // Check if the sender already exists
        val existingId = getSenderIdByAddress(trimmedAddress)
        if (existingId != null) return existingId

        // Insert new sender and return its ID
        return insertSenderAddress(
            SenderAddressEntity(
                senderAddress = trimmedAddress,
                senderType = senderType
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSmsClassificationTypes(classificationTypes: List<SmsClassificationTypeEntity>)

    @Query(
        """
    SELECT 
        sms.id AS id,
        sms.body AS body,
        sms.date AS date,
        sms.type AS type,
        classification.sms_type AS sms_message_type,
        classification.compact_sms_type AS compact_type,
        sms.importance_score AS importance_score,
        sms.confidence_score AS confidence_score
    FROM sms_messages AS sms
    LEFT JOIN sms_classification_types AS classification
        ON sms.sms_classification_type_id = classification.id
    WHERE sms.sender_address_id = :senderAddressId
    AND (:important = -1  OR classification.is_important = :important )
    ORDER BY sms.date DESC
"""
    )
    fun getPagedSmsMessagesPagedBySenderAddressId(
        senderAddressId: Long,
        important: Int
    ): PagingSource<Int, SmsMessageModel>
}