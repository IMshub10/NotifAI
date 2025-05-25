package com.summer.notifai.ui.search.searchlist

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.summer.core.domain.model.SearchSectionId
import com.summer.core.ui.BaseFragment
import com.summer.core.ui.model.SmsImportanceType
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSearchListBinding
import com.summer.notifai.ui.common.PagingLoadStateAdapter
import com.summer.notifai.ui.datamodel.GlobalSearchListItem
import com.summer.notifai.ui.inbox.SmsInboxActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchListFrag : BaseFragment<FragSearchListBinding>() {

    override val layoutResId: Int
        get() = R.layout.frag_search_list

    private val viewModel: SearchListViewModel by viewModels()

    private var _searchListPagingAdapter: SearchListPagingAdapter? = null
    private val searchListPagingAdapter
        get() = _searchListPagingAdapter!!

    private val args: SearchListFragArgs by navArgs()

    private val searchQuery: String by lazy { args.query }

    private val searchType: SearchSectionId by lazy {
        args.searchType.toIntOrNull()?.let { SearchSectionId.fromId(it) }
            ?: SearchSectionId.MESSAGES
    }

    private val senderAddressId: Long by lazy {
        args.senderAddressId.toLongOrNull() ?: 0L
    }

    private lateinit var backPressedCallback: OnBackPressedCallback

    private var contactClicked = false

    override fun onFragmentReady(instanceState: Bundle?) {
        viewModel.setSearchType(searchType)
        viewModel.initializeSearchInput(senderAddressId, searchQuery)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchView()
        setUpAdapter()
        observeViewModel()
        listeners()
    }

    private fun listeners() {
        mBinding.etFragSearchListSearch.addTextChangedListener {
            viewModel.setSearchFilter(it.toString())
        }
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (senderAddressId == 0L)
                    findNavController().popBackStack()
                else
                    requireActivity().finish()
            }
        }
        mBinding.ivFragSearchListBack.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.searchListFrag)
                if (senderAddressId == 0L)
                    findNavController().popBackStack()
                else
                    requireActivity().finish()
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingData.collectLatest { pagingData ->
                searchListPagingAdapter.submitData(pagingData)
            }
        }
    }

    private fun setUpAdapter() {
        _searchListPagingAdapter = SearchListPagingAdapter(
            query = args.query,
            itemClickListener = object : SearchListPagingAdapter.GlobalSearchItemClickListener {
                override fun onSmsClicked(item: GlobalSearchListItem.SmsItem) {
                    activity?.let {
                        startActivity(
                            SmsInboxActivity.onNewInstance(
                                context = it,
                                senderAddressId = item.data.senderAddressId,
                                smsImportanceType = SmsImportanceType.ALL,
                                targetSmsId = item.data.id
                            )
                        )
                    }
                }

                override fun onConversationClicked(item: GlobalSearchListItem.ConversationItem) {
                    activity?.let {
                        startActivity(
                            SmsInboxActivity.onNewInstance(
                                context = it,
                                senderAddressId = item.data.senderAddressId,
                                smsImportanceType = SmsImportanceType.ALL
                            )
                        )
                    }
                }

                override fun onContactClicked(item: GlobalSearchListItem.ContactItem) {
                    contactClicked = true
                    lifecycleScope.launch(Dispatchers.Default) {
                        val id = viewModel.getOrInsertSenderId(item.data)
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
        )

        mBinding.rvFragSearchListList.adapter =
            searchListPagingAdapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter { searchListPagingAdapter.retry() },
                footer = PagingLoadStateAdapter { searchListPagingAdapter.retry() }
            )
    }

    private fun initSearchView() {
        mBinding.etFragSearchListSearch.setText(searchQuery)
        mBinding.etFragSearchListSearch.post {
            mBinding.etFragSearchListSearch.requestFocus()
            mBinding.etFragSearchListSearch.setSelection(searchQuery.length)
        }
    }

    override fun onDestroyView() {
        _searchListPagingAdapter = null
        super.onDestroyView()
    }
}