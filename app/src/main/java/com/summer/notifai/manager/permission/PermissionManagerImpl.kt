package com.summer.notifai.manager.permission

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.core.content.ContextCompat

class PermissionManagerImpl(private val context: Context) : PermissionManager {

    val requiredPermissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.READ_CONTACTS
    )

    val optionalPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all { hasPermission(it) }
    }

    override fun isDefaultSMS(): Boolean {
        return true
        //for testing
        /*return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(RoleManager::class.java)
                ?.isRoleHeld(RoleManager.ROLE_SMS) == true
        } else {
            Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
        }*/
    }

    override fun hasReadSMS(): Boolean = hasPermission(Manifest.permission.READ_SMS)

    override fun hasReceiveSMS(): Boolean = hasPermission(Manifest.permission.RECEIVE_SMS)

    override fun hasReadContacts(): Boolean = hasPermission(Manifest.permission.READ_CONTACTS)

    override fun hasReadExternalStorage(): Boolean =
        hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun hasWriteExternalStorage(): Boolean =
        hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}