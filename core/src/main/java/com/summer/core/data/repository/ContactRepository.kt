package com.summer.core.data.repository

import androidx.paging.PagingSource
import com.summer.core.data.local.dao.ContactDao
import com.summer.core.android.phone.data.entity.ContactEntity
import com.summer.core.domain.repository.IContactRepository
import com.summer.core.data.local.model.ContactInfoInboxModel
import com.summer.core.data.local.model.ContactMessageInfoModel
import com.summer.core.data.local.preference.PreferenceKey
import com.summer.core.data.local.preference.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
        sharedPreferencesManager.saveData(PreferenceKey.PHONE_TABLE_LAST_UPDATED, timeInMillis)
    }

    override fun getPhoneTableLastUpdated(): Long {
        return sharedPreferencesManager.getDataLong(PreferenceKey.PHONE_TABLE_LAST_UPDATED, 0L)
    }

    override fun getPagedContactList(
        isImportant: Boolean,
    ): PagingSource<Int, ContactMessageInfoModel> {
        return contactDao.getContactListByImportancePaged(isImportant)
    }

    override fun getContactInfoBySenderAddressId(
        senderAddressId: Long,
        important: Int
    ): Flow<ContactInfoInboxModel?> {
        return contactDao.getContactInfoBySenderAddressId(
            senderAddressId = senderAddressId,
            important = important
        )
    }
}