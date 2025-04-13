package com.summer.notifai.ui.home.smscontacts

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.summer.core.ui.BaseFragment
import com.summer.core.ui.model.SmsImportanceType.Companion.toSmsImportanceType
import com.summer.notifai.R
import com.summer.notifai.databinding.FragContactListBinding
import com.summer.notifai.ui.common.PagingLoadStateAdapter
import com.summer.notifai.ui.home.SmsContactListViewModel
import com.summer.notifai.ui.inbox.SmsInboxActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmsContactListFrag : BaseFragment<FragContactListBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_contact_list

    private val smsContactListViewModel: SmsContactListViewModel by activityViewModels()

    private var _smsContactListPagingAdapter: SmsContactListPagingAdapter? = null
    private val contactListPagingAdapter
        get() = _smsContactListPagingAdapter!!

    private var lastImportanceFilter: Boolean? = null

    override fun onFragmentReady(instanceState: Bundle?) {
        super.onFragmentReady(instanceState)
        mBinding.viewModel = smsContactListViewModel
        observePagingData()
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                smsContactListViewModel.isImportant.asFlow().collectLatest { currentImportance ->
                    val isImportanceChanged = lastImportanceFilter != currentImportance
                    lastImportanceFilter = currentImportance

                    if (isImportanceChanged || _smsContactListPagingAdapter == null) {
                        setupAdapter(currentImportance)
                    }

                    smsContactListViewModel.pagedContacts.collectLatest { pagingData ->
                        contactListPagingAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun setupAdapter(currentImportance: Boolean) {
        _smsContactListPagingAdapter = SmsContactListPagingAdapter { model ->
            activity?.let {
                startActivity(
                    SmsInboxActivity.onNewInstance(
                        context = it,
                        senderAddressId = model.senderAddressId,
                        smsImportanceType = currentImportance.toSmsImportanceType()
                    )
                )
            }
        }

        mBinding.rvFragContactList.adapter = contactListPagingAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { contactListPagingAdapter.retry() },
            footer = PagingLoadStateAdapter { contactListPagingAdapter.retry() }
        )

        mBinding.rvFragContactList.itemAnimator?.apply {
            addDuration = 120
            removeDuration = 120
            changeDuration = 100
            moveDuration = 100
        }

        // Optional scroll-to-top when filter changes
        mBinding.rvFragContactList.scrollToPosition(0)
    }

    override fun onDestroyView() {
        _smsContactListPagingAdapter = null
        super.onDestroyView()
    }
}