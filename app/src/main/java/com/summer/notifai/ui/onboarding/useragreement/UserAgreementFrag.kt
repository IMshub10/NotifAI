package com.summer.notifai.ui.onboarding.useragreement

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.summer.notifai.R
import com.summer.notifai.databinding.FragUserAgreementBinding
import com.summer.notifai.ui.onboarding.OnboardingViewModel
import com.summer.core.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAgreementFrag : BaseFragment<FragUserAgreementBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_user_agreement

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    /*private var _optionsFrag: OptionalAgreementDialogFrag? = null
    private val optionsFrag: OptionalAgreementDialogFrag
        get() = _optionsFrag!!*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
    }

    private fun listeners() {
        with(mBinding) {
            fragUserAgreementActionButton.setOnClickListener {
                if (fragUserAgreementActionButton.text == getString(R.string.agree_to_use)) {
                    if (findNavController().currentDestination?.id == R.id.userAgreementFrag)
                        findNavController().navigate(R.id.action_userAgreementFrag_to_permissionsFrag)
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

    /*private fun initOptionsFrag() {
        _optionsFrag =
            OptionalAgreementDialogFrag(object : OptionalAgreementDialogFrag.ClickListener {
                override fun onAgree() {
                    onboardingViewModel.onOptionalDataSharingEnabled()
                    if (findNavController().currentDestination?.id == R.id.userAgreementFrag)
                        findNavController().navigate(R.id.action_userAgreementFrag_to_permissionsFrag)
                }

                override fun onDisagree() {
                    onboardingViewModel.onOptionalDataSharingDisabled()
                    if (findNavController().currentDestination?.id == R.id.userAgreementFrag)
                        findNavController().navigate(R.id.action_userAgreementFrag_to_permissionsFrag)
                }
            })
    }*/

    /*override fun onDestroyView() {
        super.onDestroyView()
        _optionsFrag?.dismiss()
        _optionsFrag = null
    }*/
}