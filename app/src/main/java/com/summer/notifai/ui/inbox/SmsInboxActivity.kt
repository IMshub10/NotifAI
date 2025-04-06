package com.summer.notifai.ui.inbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.summer.core.android.notification.AppNotificationManager
import com.summer.core.di.ChatSessionTracker
import com.summer.core.ui.BaseActivity
import com.summer.core.ui.SmsImportanceType
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivitySmsInboxBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SmsInboxActivity : BaseActivity<ActivitySmsInboxBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_sms_inbox

    @Inject
    lateinit var appNotificationManager: AppNotificationManager

    @Inject
    lateinit var chatSessionTracker: ChatSessionTracker

    private val viewmodel by viewModels<SmsInboxViewModel>()

    override fun onActivityReady(savedInstanceState: Bundle?) {
        setupActionBar(mBinding.mtActSmsInboxToolbar)
        initData()
        setupNavController(R.id.smsInboxFrag)
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        chatSessionTracker.activeSenderAddressId = viewmodel.senderAddressId
        viewmodel.markSmsListAsRead(context = this, senderAddressId = viewmodel.senderAddressId) {
            appNotificationManager.clearNotificationForSender(it)
        }
    }

    override fun onPause() {
        super.onPause()
        chatSessionTracker.activeSenderAddressId = null
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