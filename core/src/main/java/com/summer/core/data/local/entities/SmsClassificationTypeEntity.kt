package com.summer.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.summer.core.data.local.entities.SmsClassificationTypeEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class SmsClassificationTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "multi_label_sms_type")
    val multiLabelSmsType: String?,

    @ColumnInfo(name = "sms_type")
    val smsType: String?,

    @ColumnInfo(name = "aggregate_sms_type")
    val aggregateSmsType: String?
) {
    companion object {
        const val TABLE_NAME = "sms_classification_types"
    }
}