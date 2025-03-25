package com.summer.notifai.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.ActivityOnboardingBinding
import com.summer.passwordmanager.base.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_onboarding

    override fun onActivityReady(savedInstanceState: Bundle?) {
        val flowType = OnboardingFlowType.fromString(intent.getStringExtra(FLOW_TYPE).orEmpty())
            ?: OnboardingFlowType.USER_AGREEMENT
        val startDestination = when (flowType) {
            OnboardingFlowType.USER_AGREEMENT -> {
                R.id.userAgreementFrag
            }

            OnboardingFlowType.PERMISSIONS -> {
                R.id.permissionsFrag
            }

            OnboardingFlowType.SMS_CLASSIFICATION -> {
                R.id.userAgreementFrag //TODO(To be replaced with smsClassificationFlag)
            }
        }
        setupNavController(startDestination)
    }

    private fun setupNavController(startDestination: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController ?: throw IllegalStateException("NavController is null")

        val navGraph = navController.navInflater.inflate(R.navigation.nav_onboarding)
        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    companion object {
        const val FLOW_TYPE = "FLOW_TYPE"
        fun onNewInstance(context: Context, onboardingFlowType: OnboardingFlowType): Intent {
            return Intent(context, OnBoardingActivity::class.java).apply {
                putExtra(FLOW_TYPE, onboardingFlowType.name)
            }
        }
    }
}