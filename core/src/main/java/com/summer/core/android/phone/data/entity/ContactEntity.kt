package com.summer.core.android.phone.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ContactEntity.TABLE_NAME)
data class ContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name") // Standard field, already fine
    val name: String,

    @ColumnInfo(name = "phone_number") // Standard field, already fine
    val phoneNumber: String,

    @ColumnInfo(name = "updated_at_app")  // Row updated-at, in epoch
    val updatedAtApp: Long,
) {
    companion object {
        const val TABLE_NAME = "contacts"
    }
}