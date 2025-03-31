package com.summer.core.repository

import com.summer.core.data.local.entities.ContactEntity

interface IContactRepository {
    suspend fun getAllContacts(): List<ContactEntity>

    suspend fun getContactById(contactId: Long): ContactEntity?

    suspend fun insertOrUpdateContact(contact: ContactEntity)

    fun setPhoneTableLastUpdated(timeInMillis: Long)

    fun getPhoneTableLastUpdated(): Long
}