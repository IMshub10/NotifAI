package com.summer.notifai.ui.smsinbox.contactlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragContactListBinding
import com.summer.notifai.ui.datamodel.ContactMessageInfoDataModel
import com.summer.notifai.ui.smsinbox.SmsInboxViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactListFrag : BaseFragment<FragContactListBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_contact_list

    private val smsInboxViewModel: SmsInboxViewModel by activityViewModels()

    private lateinit var contactListPagingAdapter: ContactListPagingAdapter

    override fun onFragmentReady(instanceState: Bundle?) {
        super.onFragmentReady(instanceState)
        mBinding.viewModel = smsInboxViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePagingData()
    }

    private suspend fun setupAdapter(data: PagingData<ContactMessageInfoDataModel>) {
        // Create a fresh adapter every time
        contactListPagingAdapter = ContactListPagingAdapter {}

        mBinding.rvFragContactList.adapter = contactListPagingAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { contactListPagingAdapter.retry() },
            footer = PagingLoadStateAdapter { contactListPagingAdapter.retry() }
        )

        // Submit new data
        contactListPagingAdapter.submitData(data)

        // Optional fade animation
        mBinding.rvFragContactList.alpha = 0f
        mBinding.rvFragContactList.animate()
            .alpha(1f)
            .setDuration(250)
            .start()

        mBinding.rvFragContactList.apply {
            adapter = contactListPagingAdapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter { contactListPagingAdapter.retry() },
                footer = PagingLoadStateAdapter { contactListPagingAdapter.retry() }
            )

            // Optional: fade-in animation
            itemAnimator?.apply {
                addDuration = 120
                removeDuration = 120
                changeDuration = 100
                moveDuration = 100
            }
        }
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                smsInboxViewModel.pagedContacts.collectLatest { pagingData ->
                    setupAdapter(pagingData)
                }
            }
        }
    }
}