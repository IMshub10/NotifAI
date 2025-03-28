package com.summer.core.repository

import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.data.local.preference.PreferenceKeys
import javax.inject.Inject


class OnboardingRepository @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : IOnboardingRepository {
    override fun hasAgreedToUserAgreement(): Boolean {
        return sharedPreferencesManager.getDataBoolean(PreferenceKeys.USER_AGREEMENT)
    }

    override fun setUserAgreement(agreed: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.USER_AGREEMENT, agreed)
    }

    override fun setDataSharing(enabled: Boolean) {
        sharedPreferencesManager.saveData(PreferenceKeys.DATA_SHARING, enabled)
    }

    override fun isSMSProcessingCompleted(): Boolean {
        return sharedPreferencesManager.getDataBoolean(PreferenceKeys.SMS_PROCESSING_STATUS)
    }
}