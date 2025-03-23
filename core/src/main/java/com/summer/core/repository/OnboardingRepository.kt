package com.summer.core.repository

import com.summer.core.data.preference.SharedPreferencesManager
import com.summer.core.data.preference.PreferenceKeys
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

    // Add other methods to handle onboarding data
}