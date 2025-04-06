package com.summer.core.domain.repository

interface IOnboardingRepository {
    fun hasAgreedToUserAgreement(): Boolean
    fun setUserAgreement(agreed: Boolean)
    fun setDataSharing(enabled: Boolean)
    fun isSmsProcessingCompleted(): Boolean
    fun setPhoneTableLastUpdated(timeInMillis: Long)
    fun areContactsSynced(): Boolean
}