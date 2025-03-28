package com.summer.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.summer.core.data.local.entities.SMSEntity

@Dao
interface SMSDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(smsList: List<SMSEntity>)

    @Query("SELECT * FROM sms_messages ORDER BY date DESC, android_sms_id DESC LIMIT 1")
    suspend fun getLastInsertedSMS(): SMSEntity?

    @Query("SELECT COUNT(*) FROM sms_messages")
    suspend fun getTotalProcessedSMSCount(): Int
}