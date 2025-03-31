package com.summer.core.repository

import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.data.local.preference.PreferenceKeys
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
        return sharedPreferencesManager.getDataBoolean(PreferenceKeys.USER_AGREEMENT)
    }

    override fun setUserAgreement(agreed: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.USER_AGREEMENT, agreed)
    }

    override fun setDataSharing(enabled: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.DATA_SHARING, enabled)
    }

    override fun isSmsProcessingCompleted(): Boolean {
        return sharedPreferencesManager.getDataBoolean(PreferenceKeys.SMS_PROCESSING_STATUS)
    }

    override fun setPhoneTableLastUpdated(timeInMillis: Long) {
        sharedPreferencesManager.saveData(PreferenceKeys.PHONE_TABLE_LAST_UPDATED, timeInMillis)
    }

    override fun areContactsSynced(): Boolean {
        return sharedPreferencesManager.getDataLong(PreferenceKeys.PHONE_TABLE_LAST_UPDATED) != 0L
    }
}