package com.summer.core.repository

interface IOnboardingRepository {
    fun hasAgreedToUserAgreement(): Boolean
    fun setUserAgreement(agreed: Boolean)
}