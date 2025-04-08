package com.summer.core.android.permission.manager

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.core.content.ContextCompat

class PermissionManagerImpl(private val context: Context) : IPermissionManager {

    override val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
                Manifest.permission.SEND_SMS
            )
        } else {
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_MMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.SEND_SMS
            )
        }
    } else {
        arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.SEND_SMS
        )
    }

    val optionalPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all { hasPermission(it) }
    }

    override fun isDefaultSms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleHeld = context.getSystemService(RoleManager::class.java)
                ?.isRoleHeld(RoleManager.ROLE_SMS) == true
            val isDefault = Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
            roleHeld || isDefault
        } else {
            Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
        }
    }

    override fun hasReadSms(): Boolean = hasPermission(Manifest.permission.READ_SMS)

    override fun hasReceiveSms(): Boolean = hasPermission(Manifest.permission.RECEIVE_SMS)

    override fun hasReadContacts(): Boolean = hasPermission(Manifest.permission.READ_CONTACTS)

    override fun hasReadExternalStorage(): Boolean =
        hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun hasWriteExternalStorage(): Boolean =
        hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun hasSendNotifications(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

    override fun hasSendSms(): Boolean =
        hasPermission(Manifest.permission.SEND_SMS)

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}