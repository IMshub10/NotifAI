package com.summer.core.android.permission.manager

interface IPermissionManager {
    fun isDefaultSms(): Boolean
    fun hasReadSms(): Boolean
    fun hasReceiveSms(): Boolean
    fun hasReadContacts(): Boolean
    fun hasReadExternalStorage(): Boolean
    fun hasWriteExternalStorage(): Boolean
    fun hasSendNotifications(): Boolean
    fun hasSendSms(): Boolean
    fun hasRequiredPermissions(): Boolean

    val requiredPermissions : Array<String>
}