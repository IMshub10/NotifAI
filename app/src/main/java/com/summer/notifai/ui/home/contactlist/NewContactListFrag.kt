package com.summer.notifai.ui.home.contactlist

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.summer.core.android.sms.constants.Constants.SEARCH_NEW_CONTACT_ID
import com.summer.core.ui.BaseFragment
import com.summer.core.ui.model.SmsImportanceType
import com.summer.core.ui.model.SmsImportanceType.Companion.toSmsImportanceType
import com.summer.notifai.R
import com.summer.notifai.databinding.FragNewContactListBinding
import com.summer.notifai.ui.common.PagingLoadStateAdapter
import com.summer.notifai.ui.inbox.SmsInboxActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NewContactListFrag : BaseFragment<FragNewContactListBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_new_contact_list

    private val viewModel: NewContactListViewModel by viewModels()

    private var _contactListPagingAdapter: NewContactPagingAdapter? = null
    private val contactListPagingAdapter get() = _contactListPagingAdapter!!

    private var contactClicked = false

    override fun onFragmentReady(instanceState: Bundle?) {
        super.onFragmentReady(instanceState)
        mBinding.viewModel = viewModel
        initSearchView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        observePagingData()
        listeners()
    }

    private fun listeners() {
        mBinding.ivFragNewContactListBack.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.newContactListFrag)
                findNavController().popBackStack()
        }
        mBinding.svFragNewContactListSearch.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.contactFilter.value = newText.orEmpty()
                return true
            }
        })
    }

    private fun initSearchView() {
        mBinding.svFragNewContactListSearch.findViewById<ImageView?>(androidx.appcompat.R.id.search_close_btn)
            ?.isVisible = false
        mBinding.svFragNewContactListSearch.isIconified = false
    }

    private fun setupAdapter() {
        _contactListPagingAdapter = NewContactPagingAdapter { model ->
            if (!contactClicked) {
                contactClicked = true
                lifecycleScope.launch(Dispatchers.Default) {
                    val id = viewModel.getOrInsertSenderId(model)
                    withContext(Dispatchers.Main) {
                        activity?.let {
                            startActivity(
                                SmsInboxActivity.onNewInstance(
                                    context = it,
                                    senderAddressId = id,
                                    smsImportanceType = SmsImportanceType.IMPORTANT
                                )
                            )
                        }
                        contactClicked = false
                    }
                }
            }
        }

        mBinding.rvFragNewConthtactListList.adapter =
            contactListPagingAdapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter { contactListPagingAdapter.retry() },
                footer = PagingLoadStateAdapter { contactListPagingAdapter.retry() }
            )

        mBinding.rvFragNewConthtactListList.itemAnimator?.apply {
            addDuration = 120
            removeDuration = 120
            changeDuration = 100
            moveDuration = 100
        }
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagedContacts.collectLatest { pagingData ->
                    contactListPagingAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroyView() {
        _contactListPagingAdapter = null
        super.onDestroyView()
    }
}