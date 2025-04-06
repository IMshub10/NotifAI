package com.summer.core.data.repository

import com.summer.core.domain.repository.IOnboardingRepository
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.data.local.preference.PreferenceKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class OnboardingRepository @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val smsDao: SmsDao //TODO
) : IOnboardingRepository {
    override fun hasAgreedToUserAgreement(): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            smsDao.getTotalProcessedSmsCount()
        }
        return sharedPreferencesManager.getDataBoolean(PreferenceKey.USER_AGREEMENT)
    }

    override fun setUserAgreement(agreed: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKey.USER_AGREEMENT, agreed)
    }

    override fun setDataSharing(enabled: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKey.DATA_SHARING, enabled)
    }

    override fun isSmsProcessingCompleted(): Boolean {
        return sharedPreferencesManager.getDataBoolean(PreferenceKey.SMS_PROCESSING_STATUS)
    }

    override fun setPhoneTableLastUpdated(timeInMillis: Long) {
        sharedPreferencesManager.saveData(PreferenceKey.PHONE_TABLE_LAST_UPDATED, timeInMillis)
    }

    override fun areContactsSynced(): Boolean {
        return sharedPreferencesManager.getDataLong(PreferenceKey.PHONE_TABLE_LAST_UPDATED) != 0L
    }
}