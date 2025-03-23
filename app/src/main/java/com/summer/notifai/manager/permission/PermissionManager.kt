package com.summer.notifai.manager.permission

interface PermissionManager {
    fun isDefaultSMS(): Boolean
    fun hasReadSMS(): Boolean
    fun hasReceiveSMS(): Boolean
    fun hasReadContacts(): Boolean
    fun hasReadExternalStorage(): Boolean
    fun hasWriteExternalStorage(): Boolean
}