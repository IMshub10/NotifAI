package com.summer.core.data.preference

enum class PreferenceKeys(val key: String) {
    USER_AGREEMENT("user_agreement");

    companion object {
        const val DEFAULT_PREF = "notifai_prefs"
    }
}