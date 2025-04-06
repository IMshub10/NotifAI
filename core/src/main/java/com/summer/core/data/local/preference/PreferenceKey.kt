package com.summer.core.data.local.preference

enum class PreferenceKey(val key: String) {
    USER_AGREEMENT("user_agreement"),
    DATA_SHARING("data_sharing"),
    SMS_PROCESSING_STATUS("sms_processing_status"),
    PHONE_TABLE_LAST_UPDATED("phone_table_last_updated"),
    DONT_SHARE_SMS_DATA("dont_share_sms_data");

    companion object {
        const val DEFAULT_PREF = "notifai_prefs"
    }
}