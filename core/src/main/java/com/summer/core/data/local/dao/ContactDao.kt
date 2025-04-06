package com.summer.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.summer.core.android.phone.data.entity.ContactEntity
import com.summer.core.data.local.model.ContactInfoInboxModel
import com.summer.core.data.local.model.ContactMessageInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateContact(contact: ContactEntity)

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): ContactEntity?

    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<ContactEntity>

    @Query("DELETE FROM contacts")
    suspend fun clearContacts()

    @Query(
        """
    SELECT 
    COALESCE(c.name, sa.sender_address) AS sender_name,
    s.raw_address,
    s.body AS last_message,
    s.date AS last_message_date,
    sa.id AS sender_address_id,
    (
        SELECT COUNT(*) 
        FROM sms_messages sm
        INNER JOIN sms_classification_types sct ON sm.sms_classification_type_id = sct.id
        WHERE sm.sender_address_id = sa.id
          AND sm.read = 0
          AND sct.is_important = :isImportant
    ) AS unread_count
    FROM sender_addresses sa
    INNER JOIN sms_messages s ON s.id = (
    SELECT sm.id
    FROM sms_messages sm
    INNER JOIN sms_classification_types sct ON sm.sms_classification_type_id = sct.id
    WHERE sm.sender_address_id = sa.id
      AND sct.is_important = :isImportant
    ORDER BY sm.date DESC
    LIMIT 1
)
LEFT JOIN contacts c ON c.phone_number = sa.sender_address
GROUP BY s.id
ORDER BY s.date DESC
"""
    )
    fun getContactListByImportancePaged(isImportant: Boolean): PagingSource<Int, ContactMessageInfoModel>

    @Query(
        """
    SELECT 
    COALESCE(c.name, sa.sender_address) AS sender_name,
    sa.id AS sender_address_id,
    (
        SELECT COUNT(*) 
        FROM sms_messages sm
        INNER JOIN sms_classification_types sct ON sm.sms_classification_type_id = sct.id
        WHERE sm.sender_address_id = sa.id
          AND sm.read = 0
          AND (:important = -1 OR sct.is_important = :important )
    ) AS unread_count
    FROM sender_addresses sa
LEFT JOIN contacts c ON c.phone_number = sa.sender_address
where sa.id = :senderAddressId
    """
    )
    fun getContactInfoBySenderAddressId(
        senderAddressId: Long,
        important: Int
    ): Flow<ContactInfoInboxModel?>

    @Query("""
    SELECT COALESCE(c.name, sa.sender_address) AS sender_name
    FROM sender_addresses sa
    LEFT JOIN contacts c ON c.phone_number = sa.sender_address
    WHERE sa.id = :senderAddressId
    LIMIT 1
""")
    suspend fun getSenderNameById(senderAddressId: Long): String
}