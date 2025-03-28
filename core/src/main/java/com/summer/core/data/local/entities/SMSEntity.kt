package com.summer.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_messages")
data class SMSEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "android_sms_id")  // Unique SMS ID from the system
    val androidSMSId: Int?,

    @ColumnInfo(name = "address")  // Sender/Receiver Number
    val address: String,

    @ColumnInfo(name = "body")  // SMS content
    val body: String,

    @ColumnInfo(name = "date")  // Received/Sent Timestamp
    val date: Long,

    @ColumnInfo(name = "date_sent")  // When the sender originally sent it
    val dateSent: Long?,

    @ColumnInfo(name = "type")  // Sent (2) or Received (1)
    val type: Int,

    @ColumnInfo(name = "thread_id")  // Thread/conversation ID
    val threadId: Int?,

    @ColumnInfo(name = "read")  // 0 = Unread, 1 = Read
    val read: Int,

    @ColumnInfo(name = "status")  // -1 = None, 0 = Complete, 32 = Pending, 64 = Failed
    val status: Int?,

    @ColumnInfo(name = "service_center")  // SMS Service Center Address
    val serviceCenter: String?,

    @ColumnInfo(name = "subscription_id")  // SIM ID (Useful for dual SIM)
    val subscriptionId: Int?,

    @ColumnInfo(name = "classification")  // Bert classification
    val classification: String?,

    @ColumnInfo(name = "confidence_score")  // Bert classification
    val confidenceScore: Float?,

    @ColumnInfo(name = "created_at_app")  // Row created-at, in epoch
    val createdAtApp: Long,

    @ColumnInfo(name = "updated_at_app")  // Row updated-at, in epoch
    val updatedAtApp: Long,
)