package com.summer.core.android.permission

interface PermissionManager {
    fun isDefaultSms(): Boolean
    fun hasReadSms(): Boolean
    fun hasReceiveSms(): Boolean
    fun hasReadContacts(): Boolean
    fun hasReadExternalStorage(): Boolean
    fun hasWriteExternalStorage(): Boolean
}