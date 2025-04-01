package com.summer.core.data.local.db

import com.summer.core.data.local.entities.SmsClassificationTypeEntity

object DataSet {
    val smsClassificationTypes = listOf(
        SmsClassificationTypeEntity(
            1, "ACADEMIC_UPDATE", "ACADEMIC_UPDATE", "IMPORTANT_UPDATE", true
        ),
        SmsClassificationTypeEntity(
            2, "BANKING_TRANSACTION", "BANKING_TRANSACTION", "BANKING_TRANSACTION", true
        ),
        SmsClassificationTypeEntity(
            3, "BANKING_TRANSACTION_REWARD", "BANK_REWARD", "REWARD", true
        ),
        SmsClassificationTypeEntity(
            4, "BANKING_UPDATE", "BANKING_UPDATE", "BANKING_UPDATE", true
        ),
        SmsClassificationTypeEntity(
            5, "BANKING_DUE_UPDATE_REMINDER", "BANK_DUE_REMINDER", "DUE_REMINDER", true
        ),
        SmsClassificationTypeEntity(
            6,
            "DELIVERY_OR_SERVICE_CODE",
            "DELIVERY_OR_SERVICE_CODE",
            "DELIVERY_OR_SERVICE_CODE",
            true
        ),
        SmsClassificationTypeEntity(
            7, "DELIVERY_UPDATE", "DELIVERY_UPDATE", "DELIVERY_UPDATE", true
        ),
        SmsClassificationTypeEntity(
            8, "DUE_REMINDER", "DUE_REMINDER", "DUE_REMINDER", true
        ),
        SmsClassificationTypeEntity(
            9, "EMERGENCY_ALERT", "EMERGENCY_ALERT", "EMERGENCY_ALERT", true
        ),
        SmsClassificationTypeEntity(
            10, "EQUITY_NOTIFICATION", "EQUITY_NOTIFICATION", "EQUITY_NOTIFICATION", true
        ),
        SmsClassificationTypeEntity(
            11, "EQUITY_NOTIFICATION_DUE_REMINDER", "EQUITY_DUE_REMINDER", "DUE_REMINDER", true
        ),
        SmsClassificationTypeEntity(
            12, "IMPORTANT_UPDATE", "IMPORTANT_UPDATE", "IMPORTANT_UPDATE", true
        ),
        SmsClassificationTypeEntity(
            13, "INSURANCE_NOTIFICATION", "INSURANCE_NOTIFICATION", "INSURANCE_NOTIFICATION", false
        ),
        SmsClassificationTypeEntity(
            14,
            "INSURANCE_NOTIFICATION_DUE_REMINDER",
            "INSURANCE_DUE_REMINDER",
            "DUE_REMINDER",
            true
        ),
        SmsClassificationTypeEntity(15, "INVOICE", "INVOICE", "INVOICE", true),
        SmsClassificationTypeEntity(
            16, "LOAN_OFFER", "LOAN_OFFER", "LOAN_OFFER", false
        ),
        SmsClassificationTypeEntity(
            17, "MISSED_CALL_UPDATE", "MISSED_CALL_UPDATE", "MISSED_CALL_UPDATE", false
        ),
        SmsClassificationTypeEntity(18, "OFFER", "OFFER", "OFFER", true),
        SmsClassificationTypeEntity(
            19, "OFFER_DUE_REMINDER", "OFFER_DUE_REMINDER", "DUE_REMINDER", true
        ),
        SmsClassificationTypeEntity(
            20, "OTP_VERIFICATION_CODE", "OTP_VERIFICATION_CODE", "OTP_VERIFICATION_CODE", true
        ),
        SmsClassificationTypeEntity(
            21,
            "OTP_VERIFICATION_CODE,BANKING_TRANSACTION",
            "BANKING_TRANSACTION_OTP",
            "OTP_VERIFICATION_CODE",
            true
        ),
        SmsClassificationTypeEntity(22, "PERSONAL", "PERSONAL", "PERSONAL", true),
        SmsClassificationTypeEntity(
            23, "PROMOTIONAL", "PROMOTIONAL", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            24, "PROMOTIONAL,ACADEMIC_UPDATE", "PROMOTIONAL_ACADEMIC_UPDATE", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            25, "PROMOTIONAL,BANKING_UPDATE", "PROMOTIONAL_BANKING_UPDATE", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            26,
            "PROMOTIONAL,EQUITY_NOTIFICATION",
            "PROMOTIONAL_EQUITY_NOTIFICATION",
            "PROMOTIONAL",
            false
        ),
        SmsClassificationTypeEntity(
            27,
            "PROMOTIONAL,INSURANCE_NOTIFICATION",
            "PROMOTIONAL_INSURANCE_NOTIFICATION",
            "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            28, "PROMOTIONAL,OFFER", "PROMOTIONAL_OFFER", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            29, "PROMOTIONAL,SCAM", "PROMOTIONAL_SCAM", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            30, "PROMOTIONAL,TAX_NOTIFICATION", "PROMOTIONAL_TAX_NOTIFICATION", "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            31,
            "PROMOTIONAL,TELECOM_NOTIFICATION",
            "PROMOTIONAL_TELECOM_NOTIFICATION",
            "PROMOTIONAL", false
        ),
        SmsClassificationTypeEntity(
            32,
            "PROMOTIONAL,TRAVEL_NOTIFICATION",
            "PROMOTIONAL_TRAVEL_NOTIFICATION",
            "PROMOTIONAL",
            false
        ),
        SmsClassificationTypeEntity(33, "REWARD", "REWARD", "REWARD", true),
        SmsClassificationTypeEntity(
            34, "REWARD_USED", "REWARD_USED", "REWARD_USED", true
        ),
        SmsClassificationTypeEntity(35, "SCAM", "SCAM", "SCAM", false),
        SmsClassificationTypeEntity(
            36, "SCAM_AWARENESS", "SCAM_AWARENESS", "SCAM_AWARENESS", false
        ),
        SmsClassificationTypeEntity(
            37, "SERVICE_NOTIFICATION", "SERVICE_NOTIFICATION", "SERVICE_NOTIFICATION", true
        ),
        SmsClassificationTypeEntity(
            38, "SERVICE_RECHARGE", "SERVICE_RECHARGE", "SERVICE_RECHARGE", true
        ),
        SmsClassificationTypeEntity(
            39,
            "SERVICE_RECHARGE_DUE_REMINDER",
            "SERVICE_RECHARGE_DUE_REMINDER",
            "DUE_REMINDER",
            true
        ),
        SmsClassificationTypeEntity(
            40, "SOCIAL_AWARENESS", "SOCIAL_AWARENESS", "SOCIAL_AWARENESS", false
        ),
        SmsClassificationTypeEntity(
            41, "SYSTEM_AUTH_KEY", "SYSTEM_AUTH_KEY", "SYSTEM_AUTH_KEY", false
        ),
        SmsClassificationTypeEntity(
            42, "TAX_NOTIFICATION", "TAX_NOTIFICATION", "TAX_NOTIFICATION", true
        ),
        SmsClassificationTypeEntity(
            43, "TAX_NOTIFICATION_DUE_REMINDER", "TAX_DUE_REMINDER", "TAX_NOTIFICATION", true
        ),
        SmsClassificationTypeEntity(
            44, "TELECOM_NOTIFICATION", "TELECOM_NOTIFICATION", "TELECOM_NOTIFICATION", false
        ),
        SmsClassificationTypeEntity(
            45,
            "TELECOM_NOTIFICATION_DUE_REMINDER",
            "TELECOM_NOTIFICATION_DUE_REMINDER",
            "TELECOM_NOTIFICATION", false
        ),
        SmsClassificationTypeEntity(
            46, "TRAVEL_NOTIFICATION", "TRAVEL_NOTIFICATION", "TRAVEL_NOTIFICATION", true
        )
    )
}