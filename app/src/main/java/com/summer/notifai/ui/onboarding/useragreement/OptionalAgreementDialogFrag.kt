package com.summer.notifai.ui.onboarding.useragreement

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.summer.core.ui.BaseDialogFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.DialogFragOptionalAgreementBinding
import dagger.hilt.android.AndroidEntryPoint

class OptionalAgreementDialogFrag(private val clickListener: ClickListener) :
    BaseDialogFragment<DialogFragOptionalAgreementBinding>() {
    override val layoutResId: Int
        get() = R.layout.dialog_frag_optional_agreement

    override fun onFragmentReady(instanceState: Bundle?) {
        clickListeners()
    }

    private fun clickListeners() {
        with(mBinding) {
            dialogOptionalAgreementPositiveButton.setOnClickListener {
                clickListener.onAgree()
                dismiss()
            }
            dialogOptionalAgreementNegativeButton.setOnClickListener {
                clickListener.onDisagree()
                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = Dialog(requireActivity()).apply {
        window?.attributes?.windowAnimations = R.style.SpannableDialogAnimation
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Define the margin (in pixels)
        val marginPx =
            resources.getDimension(com.summer.core.R.dimen.spacing_large) // Adjust this value for desired margin

        // Set content view with transparent background
        setContentView(RelativeLayout(requireActivity()).apply {
            setBackgroundColor(
                ContextCompat.getColor(requireContext(), android.R.color.transparent)
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,  // Reduce width by margin on both sides
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set dialog width and height with margins applied
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, // Width with horizontal margin
            ViewGroup.LayoutParams.WRAP_CONTENT // Height remains wrap_content
        )

        //Padding
        window?.decorView?.setPadding(marginPx.toInt())

        window?.setDimAmount(0.7F)

        window?.setGravity(Gravity.BOTTOM) // Align to bottom
    }

    interface ClickListener {
        fun onAgree()
        fun onDisagree()
    }
}