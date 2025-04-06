package com.summer.notifai.ui.inbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.ui.BaseActivity
import com.summer.core.ui.SmsImportanceType
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySmsInboxBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmsInboxActivity : BaseActivity<ActivitySmsInboxBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_sms_inbox

    private val viewmodel by viewModels<SmsInboxViewModel>()

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupActionBar(mBinding.mtActSmsInboxToolbar)
        initData()
        setupNavController(R.id.smsInboxFrag)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewmodel.contactInfoModel.observe(this) {
            it?.let { contact ->
                mBinding.mtActSmsInboxToolbar.title = contact.senderName
            }
        }
    }

    private fun initData() {
        val senderAddressId = intent.getLongExtra(KEY_SENDER_ADDRESS_ID, 0L)
        viewmodel.setContactInfoModel(
            senderAddressId = senderAddressId,
            smsImportanceType = SmsImportanceType.fromValue(
                intent.getIntExtra(
                    KEY_IMPORTANT, -1
                )
            ) ?: SmsImportanceType.ALL
        )
        viewmodel.markSmsListAsRead(context = this, senderAddressId = senderAddressId)
    }

    private fun setupNavController(startDestination: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_actSmsInbox_navHost) as? NavHostFragment
        val navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_sms_inbox)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    companion object {
        private const val KEY_SENDER_ADDRESS_ID = "sender_address_id"
        private const val KEY_IMPORTANT = "important"

        fun onNewInstance(
            context: Context,
            senderAddressId: Long,
            smsImportanceType: SmsImportanceType
        ): Intent {
            return Intent(context, SmsInboxActivity::class.java).apply {
                putExtra(KEY_SENDER_ADDRESS_ID, senderAddressId)
                putExtra(KEY_IMPORTANT, smsImportanceType.value)
            }
        }
    }
}