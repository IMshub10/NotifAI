package com.summer.notifai.ui.settings.blocklist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.summer.core.ui.BaseFragment
import com.summer.notifai.R
import com.summer.notifai.databinding.FragBlockListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BlockListFrag : BaseFragment<FragBlockListBinding>() {

    override val layoutResId: Int
        get() = R.layout.frag_block_list

    private val viewModel: BlockListViewModel by viewModels()
    private lateinit var blockListAdapter: BlockListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupListeners()
        observeBlockedSenders()
    }

    private fun setupAdapter() {
        blockListAdapter = BlockListAdapter(
            {

            }, { sender ->
                showYesNoDialog(
                    requireContext(),
                    title = getString(R.string.unblock_sender),
                    message = getString(R.string.are_you_sure_you_want_to_unblock_this_sender_you_ll_start_receiving_notifications_and_messages_will_appear_in_your_inbox_again),
                    {
                        viewModel.unblockSender(sender.senderAddressId) {

                        }
                    }, {}
                )
            }
        )
        mBinding.rvFragBlockListList.adapter = blockListAdapter
    }

    private fun setupListeners() {
        mBinding.ivFragBlockListBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.etFragBlockListSearch.addTextChangedListener {
            viewModel.setSearchQuery(it.toString())
        }
    }

    private fun showYesNoDialog(
        context: Context,
        title: String,
        message: String,
        onYes: () -> Unit,
        onNo: () -> Unit = {}
    ) {
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                onYes()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
                onNo()
            }
            .show()
    }

    private fun observeBlockedSenders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.blockedSenders.collectLatest { pagingData ->
                blockListAdapter.submitData(pagingData)
            }
        }
    }
}