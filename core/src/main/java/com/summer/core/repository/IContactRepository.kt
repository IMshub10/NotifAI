package com.summer.core.repository

import androidx.paging.PagingSource
import com.summer.core.data.local.entities.ContactEntity
import com.summer.core.data.local.model.ContactMessageInfoModel

interface IContactRepository {
    suspend fun getAllContacts(): List<ContactEntity>

    suspend fun getContactById(contactId: Long): ContactEntity?

    suspend fun insertOrUpdateContact(contact: ContactEntity)

    fun setPhoneTableLastUpdated(timeInMillis: Long)

    fun getPhoneTableLastUpdated(): Long

    fun getPagedContactList(isImportant: Boolean): PagingSource<Int, ContactMessageInfoModel>
}