package com.summer.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.summer.core.data.local.entities.ContactEntity
import com.summer.core.data.local.model.ContactMessageInfoModel

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

    @Query("""
    SELECT 
    COALESCE(c.name, sa.sender_address) AS sender_name,
    s.raw_address,
    s.body AS last_message,
    s.date AS last_message_date,
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
""")
    fun getContactListByImportancePaged(isImportant: Boolean): PagingSource<Int, ContactMessageInfoModel>
}