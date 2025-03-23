package com.summer.notifai.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivityOnboardingBinding
import com.summer.passwordmanager.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_onboarding
    override fun onActivityReady(savedInstanceState: Bundle?) {
        // Do something
    }

    companion object {
        const val FLOW_TYPE = "FLOW_TYPE"
        fun onNewInstance(context: Context, onboardingFlowType: OnboardingFlowType): Intent {
            return Intent(context, OnBoardingActivity::class.java).apply {
                putExtra(FLOW_TYPE, onboardingFlowType)
            }
        }
    }
}