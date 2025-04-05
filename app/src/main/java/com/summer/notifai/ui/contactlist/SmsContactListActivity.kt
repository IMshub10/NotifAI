package com.summer.notifai.ui.contactlist

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.util.showShortToast
import com.summer.core.android.permission.PermissionManagerImpl
import com.summer.core.ui.BaseActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySmsInboxBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmsContactListActivity : BaseActivity<ActivitySmsInboxBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_contact_list

    private val permissionManager by lazy {
        PermissionManagerImpl(this)
    }

    private val defaultSmsAppLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == Activity.RESULT_OK) {
            showShortToast("Permission set as default")
        } else {
            showShortToast("App must be set as default SMS app to function properly.")
            fallbackToLegacyIntent()
        }
    }

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupNavController(R.id.contactListFrag)
        if (!permissionManager.isDefaultSms()) {
            promptToSetDefaultSmsApp()
        }
    }

    private fun fallbackToLegacyIntent() {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        startActivity(intent)
    }

    private fun setupNavController(startDestination: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_contactList_navHost) as? NavHostFragment
        val navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_contact_list)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    /**
     * Step 1: Ask the user to set this app as the default SMS app.
     */
    private fun promptToSetDefaultSmsApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager =
                getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            defaultSmsAppLauncher.launch(intent)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivity(intent)
        }
    }
}
