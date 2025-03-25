package com.summer.notifai.ui.start

import android.os.Bundle
import androidx.activity.viewModels
import com.summer.core.util.startActivityWithClearTop
import com.summer.notifai.MainActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivityStartBinding
import com.summer.notifai.manager.permission.PermissionManagerImpl
import com.summer.notifai.ui.onboarding.OnBoardingActivity
import com.summer.notifai.ui.onboarding.OnboardingFlowType
import com.summer.passwordmanager.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : BaseActivity<ActivityStartBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_start

    private val startViewModel: StartViewModel by viewModels()

    private val permissionManager by lazy {
        PermissionManagerImpl(this)
    }

    override fun onActivityReady(savedInstanceState: Bundle?) {
        observers()
    }

    private fun observers() {
        startViewModel.hasAgreedToUserAgreement.observe(this) { userAgreement ->
            if (userAgreement != null) {
                val hasRequiredPermissions = permissionManager.hasRequiredPermissions()
                when {
                    userAgreement && hasRequiredPermissions -> {
                        startActivityWithClearTop(this, MainActivity::class.java)
                    }

                    !userAgreement -> {
                        startActivityWithClearTop(
                            this,
                            OnBoardingActivity.onNewInstance(
                                context = this,
                                onboardingFlowType = OnboardingFlowType.USER_AGREEMENT
                            )
                        )
                    }

                    else -> {
                        startActivityWithClearTop(
                            this,
                            OnBoardingActivity.onNewInstance(
                                context = this,
                                onboardingFlowType = OnboardingFlowType.PERMISSIONS
                            )
                        )
                    }
                }
            }
        }
    }
}