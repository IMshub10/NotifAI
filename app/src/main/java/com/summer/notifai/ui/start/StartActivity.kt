package com.summer.notifai.ui.start

import android.os.Bundle
import androidx.activity.viewModels
import com.summer.core.ml.model.SmsClassifierModel
import com.summer.core.util.startActivityWithClearTop
import com.summer.notifai.MainActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivityStartBinding
import com.summer.notifai.manager.permission.PermissionManagerImpl
import com.summer.notifai.ui.onboarding.OnBoardingActivity
import com.summer.notifai.ui.onboarding.OnboardingFlowType
import com.summer.core.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : BaseActivity<ActivityStartBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_start

    @Inject
    lateinit var smsClassifierModel: SmsClassifierModel

    private val startViewModel: StartViewModel by viewModels()

    private val permissionManager by lazy {
        PermissionManagerImpl(this)
    }

    override fun onActivityReady(savedInstanceState: Bundle?) {
        observers()
    }

    private fun observers() {
        startViewModel.uiState.observe(this) { uiState ->
            if (uiState.hasAgreedToUserAgreement != null && uiState.isSmsProcessingCompleted != null) {
                val hasRequiredPermissions = permissionManager.hasRequiredPermissions()
                when {
                    uiState.hasAgreedToUserAgreement && uiState.isSmsProcessingCompleted -> {
                        startActivityWithClearTop(this, MainActivity::class.java)
                    }

                    !uiState.hasAgreedToUserAgreement -> {
                        startActivityWithClearTop(
                            this,
                            OnBoardingActivity.onNewInstance(
                                context = this,
                                onboardingFlowType = OnboardingFlowType.USER_AGREEMENT
                            )
                        )
                    }

                    !hasRequiredPermissions -> {
                        startActivityWithClearTop(
                            this,
                            OnBoardingActivity.onNewInstance(
                                context = this,
                                onboardingFlowType = OnboardingFlowType.PERMISSIONS
                            )
                        )
                    }

                    else -> {
                        startActivityWithClearTop(
                            this,
                            OnBoardingActivity.onNewInstance(
                                context = this,
                                onboardingFlowType = OnboardingFlowType.SMS_PROCESSING
                            )
                        )
                    }
                }
            }
        }
    }
}