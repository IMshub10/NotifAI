package com.summer.notifai.ui.onboarding

enum class OnboardingFlowType {
    USER_AGREEMENT,
    PERMISSIONS,
    SMS_CLASSIFICATION;

    companion object {
        fun fromString(value: String): OnboardingFlowType? {
            return entries.find { it.name.equals(value, ignoreCase = false) }
        }
    }

    override fun toString(): String {
        return name.lowercase() // Returns lowercase string representation
    }
}