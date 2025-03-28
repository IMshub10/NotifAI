package com.summer.notifai.ui.onboarding.processor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.summer.core.android.sms.service.SmsProcessingService
import com.summer.core.android.sms.util.ServiceUtils
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.util.startActivityWithClearTop
import com.summer.notifai.MainActivity
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSmsProcessingBinding
import com.summer.notifai.ui.onboarding.OnboardingViewModel
import com.summer.passwordmanager.base.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SMSProcessingFrag : BaseFragment<FragSmsProcessingBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_sms_processing

    private val smsProcessingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SmsProcessingService.ACTION_SMS_PROCESSING_UPDATE) {
                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(
                        SmsProcessingService.EXTRA_FETCH_RESULT,
                        FetchResult::class.java
                    )
                } else {
                    intent.getSerializableExtra(SmsProcessingService.EXTRA_FETCH_RESULT) as? FetchResult
                }
                result?.let { onboardingViewModel.updateFetchResult(it) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            smsProcessingReceiver,
            IntentFilter(SmsProcessingService.ACTION_SMS_PROCESSING_UPDATE)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(smsProcessingReceiver)
        super.onStop()
    }

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onFragmentReady(instanceState: Bundle?) {
        observeViewModel()
    }

    private fun observeViewModel() {
        onboardingViewModel.fetchStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is FetchResult.Loading -> {
                    mBinding.pgProgressIndicator.visibility = View.VISIBLE
                    val progress =
                        (result.batchNumber.toFloat() / result.totalBatches * 100).toInt()
                    mBinding.pgProgressIndicator.progress = progress
                    mBinding.tvStatusIndicator.text =
                        getString(R.string.progress_percentage, progress)
                }

                is FetchResult.Success -> {
                    mBinding.pgProgressIndicator.progress = 100
                    mBinding.tvStatusIndicator.text = getString(R.string.processing_complete)
                    mBinding.tvStatusIndicator.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivityWithClearTop(requireActivity(), MainActivity::class.java)
                    }, 400L)
                }

                is FetchResult.Error -> {
                    mBinding.tvStatusIndicator.text =
                        getString(R.string.processing_error, result.exception.message)
                    mBinding.tvStatusIndicator.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!ServiceUtils.isServiceRunning(
                requireContext(),
                SmsProcessingService::class.java
            )
        ) SmsProcessingService.startService(requireContext())
    }
}