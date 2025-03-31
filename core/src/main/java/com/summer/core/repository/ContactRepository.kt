package com.summer.core.repository

import com.summer.core.data.local.dao.ContactDao
import com.summer.core.data.local.entities.ContactEntity
import com.summer.core.data.local.preference.PreferenceKeys
import com.summer.core.data.local.preference.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val sharedPreferencesManager: SharedPreferencesManager
) : IContactRepository {

    override suspend fun getAllContacts(): List<ContactEntity> = withContext(Dispatchers.IO) {
        contactDao.getAllContacts()
    }

    override suspend fun getContactById(contactId: Long): ContactEntity? =
        withContext(Dispatchers.IO) {
            contactDao.getContactById(contactId)
        }

    override suspend fun insertOrUpdateContact(contact: ContactEntity) =
        withContext(Dispatchers.IO) {
            contactDao.insertOrUpdateContact(contact)
        }


    override fun setPhoneTableLastUpdated(timeInMillis: Long) {
        sharedPreferencesManager.saveData(PreferenceKeys.PHONE_TABLE_LAST_UPDATED, timeInMillis)
    }

    override fun getPhoneTableLastUpdated(): Long {
        return sharedPreferencesManager.getDataLong(PreferenceKeys.PHONE_TABLE_LAST_UPDATED, 0L)
    }
}