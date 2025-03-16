package com.summer.notifai

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.summer.notifai.manager.permission.PermissionManagerImpl
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private val permissionManager by lazy {
        PermissionManagerImpl(this)
    }
    private val readSMSPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (permissionManager.hasReadSMS()) {
                //TODO
            } else {
                showShortToast("App requires read sms permission to function.")
            }
        }

    private val defaultPermissionAppLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == Activity.RESULT_OK) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!permissionManager.isDefaultSMS()) {
            showDefaultSmsDialog()
        } else if (!permissionManager.hasReadSMS()) {
            readSMSPermissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    private fun showDefaultSmsDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            defaultPermissionAppLauncher.launch(intent)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivity(intent)
        }
    }

}

fun Activity.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}