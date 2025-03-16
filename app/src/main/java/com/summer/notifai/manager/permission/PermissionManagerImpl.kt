package com.summer.notifai.manager.permission

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.core.content.ContextCompat

class PermissionManagerImpl(private val context: Context) : PermissionManager {

    override fun isDefaultSMS(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(RoleManager::class.java)?.isRoleHeld(RoleManager.ROLE_SMS) == true
        } else {
            Telephony.Sms.getDefaultSmsPackage(context) == context.packageName
        }
    }

    override fun hasReadSMS(): Boolean {
        return hasPermission(Manifest.permission.READ_SMS)
    }

    override fun hasReceiveSMS(): Boolean {
        return hasPermission(Manifest.permission.RECEIVE_SMS)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}