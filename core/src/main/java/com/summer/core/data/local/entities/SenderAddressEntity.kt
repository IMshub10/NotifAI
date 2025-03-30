package com.summer.core.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.summer.core.data.local.entities.SenderAddressEntity.Companion.TABLE_NAME

@Entity(
    tableName = TABLE_NAME,
    indices = [Index(value = ["sender_address"], unique = true)],
)
data class SenderAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "sender_address")
    val senderAddress: String,

    @ColumnInfo(name = "sender_type")
    val senderType: SenderType
) {
    companion object {
        const val TABLE_NAME = "sender_addresses"
    }
}

enum class SenderType {
    BUSINESS, CONTACT
}