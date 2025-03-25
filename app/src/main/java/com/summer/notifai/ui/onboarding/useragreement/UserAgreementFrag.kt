package com.summer.notifai.ui.onboarding.useragreement

import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.summer.notifai.R
import com.summer.notifai.databinding.FragUserAgreementBinding
import com.summer.notifai.ui.onboarding.OnboardingViewModel
import com.summer.passwordmanager.base.ui.BaseFragment

class UserAgreementFrag : BaseFragment<FragUserAgreementBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_user_agreement

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private val optionsFrag by lazy {
        OptionalAgreementDialogFrag(object : OptionalAgreementDialogFrag.ClickListener {
            override fun onAgree() {
                onboardingViewModel.onOptionalDataSharingEnabled()
                findNavController().navigate(R.id.action_userAgreementFrag_to_permissionsFrag)
            }

            override fun onDisagree() {
                onboardingViewModel.onOptionalDataSharingDisabled()
                findNavController().navigate(R.id.action_userAgreementFrag_to_permissionsFrag)
            }
        })
    }

    override fun onFragmentReady(instanceState: Bundle?) {
        listeners()
    }

    private fun listeners() {
        with(mBinding) {
            fragUserAgreementActionButton.setOnClickListener {
                if (fragUserAgreementActionButton.text == getString(R.string.agree_to_use)) {
                    optionsFrag.show(childFragmentManager, null)
                } else {
                    fragUserAgreementScrollView.smoothScrollTo(
                        0,
                        fragUserAgreementScrollView.getChildAt(0).bottom
                    )
                }
            }
            fragUserAgreementScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
                if (v.getChildAt(0).bottom <= (v.height + v.scrollY)) {
                    fragUserAgreementActionButton.text = getString(R.string.agree_to_use)
                }
            })
        }
    }
}