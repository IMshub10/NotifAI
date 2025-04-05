package com.summer.notifai.ui.inbox

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summer.core.android.sms.constants.Constants.DATE_FLOATER_SHOW_TIME
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSmsInboxBinding
import com.summer.notifai.ui.contactlist.PagingLoadStateAdapter
import com.summer.notifai.ui.datamodel.SmsInboxListItem
import com.summer.core.util.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmsInboxFrag : BaseFragment<FragSmsInboxBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_sms_inbox

    private val smsInboxViewModel: SmsInboxViewModel by activityViewModels()

    private var _smsInboxPagingAdapter: SmsInboxPagingAdapter? = null
    private val smsInboxPagingAdapter
        get() = _smsInboxPagingAdapter!!

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollHideRunnable: Runnable? = null

    private var isAtBottom = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observePagingData()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = false
        }

        mBinding.rvSmsMessages.layoutManager = layoutManager

        _smsInboxPagingAdapter = SmsInboxPagingAdapter {}

        mBinding.rvSmsMessages.adapter = smsInboxPagingAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { smsInboxPagingAdapter.retry() },
            footer = PagingLoadStateAdapter { smsInboxPagingAdapter.retry() }
        )

        mBinding.rvSmsMessages.alpha = 0f
        mBinding.rvSmsMessages.animate().alpha(1f).setDuration(250).start()

        mBinding.rvSmsMessages.itemAnimator?.apply {
            addDuration = 120
            removeDuration = 120
            changeDuration = 100
            moveDuration = 100
        }

        mBinding.rvSmsMessages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val topPos = lm.findLastVisibleItemPosition()

                val visibleTop = lm.findFirstVisibleItemPosition()
                isAtBottom = visibleTop <= 1

                // Floating date label logic
                if (topPos != RecyclerView.NO_POSITION) {
                    val item = smsInboxPagingAdapter.peek(topPos) ?: return
                    val label = when (item) {
                        is SmsInboxListItem.Message -> DateUtils.formatDayHeader(item.data.dateInEpoch)
                        is SmsInboxListItem.Header -> item.header.label
                    }
                    showFloatingDateLabel(label)
                }
            }
        })
    }

    private fun showFloatingDateLabel(label: String) {
        mBinding.tvFragSmsInboxFloatingDate.text = label
        mBinding.tvFragSmsInboxFloatingDate.visibility = View.VISIBLE

        scrollHideRunnable?.let { scrollHandler.removeCallbacks(it) }

        scrollHideRunnable = Runnable {
            mBinding.tvFragSmsInboxFloatingDate.visibility = View.GONE
        }
        scrollHideRunnable?.let {
            scrollHandler.postDelayed(it, DATE_FLOATER_SHOW_TIME)
        }
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                smsInboxViewModel.pagedSmsData.collectLatest { pagingData ->
                    setupAdapter(pagingData)
                }
            }
        }
    }

    private suspend fun setupAdapter(data: PagingData<SmsInboxListItem>) {
        smsInboxPagingAdapter.submitData(data)
        smsInboxPagingAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && smsInboxPagingAdapter.itemCount > 0 && isAtBottom) {
                mBinding.rvSmsMessages.scrollToPosition(0) // Scroll to bottom
            }
        }
    }

    override fun onDestroyView() {
        scrollHideRunnable?.let { scrollHandler.removeCallbacks(it) }
        _smsInboxPagingAdapter = null
        super.onDestroyView()
    }
}