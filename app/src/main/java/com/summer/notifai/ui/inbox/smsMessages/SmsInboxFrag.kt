package com.summer.notifai.ui.inbox.smsMessages

import android.app.Activity
import android.app.ProgressDialog
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summer.core.android.permission.manager.IPermissionManager
import com.summer.core.android.sms.constants.Constants.DATE_FLOATER_SHOW_TIME
import com.summer.core.ui.BaseFragment
import com.summer.core.util.DateUtils
import com.summer.core.util.showShortToast
import com.summer.notifai.R
import com.summer.notifai.databinding.FragSmsInboxBinding
import com.summer.notifai.ui.datamodel.SmsInboxListItem
import com.summer.notifai.ui.inbox.SmsInboxViewModel
import com.summer.notifai.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsInboxFrag : BaseFragment<FragSmsInboxBinding>() {
    override val layoutResId: Int
        get() = R.layout.frag_sms_inbox

    private val smsInboxViewModel: SmsInboxViewModel by activityViewModels()

    private var _smsInboxAdapter: SmsInboxAdapter? = null
    private val smsInboxAdapter get() = _smsInboxAdapter!!

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollHideRunnable: Runnable? = null

    private val progressDialog by lazy {
        ProgressDialog(requireContext()).apply {
            setCancelable(false)
            setTitle(getString(R.string.syncing))
        }
    }

    @Inject
    lateinit var permissionManager: IPermissionManager

    private val defaultSmsAppLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult? ->
        if (result?.resultCode == Activity.RESULT_OK) {
            showShortToast("Permission set as default")
        } else {
            showShortToast("App must be set as default SMS app to function properly.")
            fallbackToLegacyIntent()
        }
    }

    override fun onFragmentReady(instanceState: Bundle?) {
        super.onFragmentReady(instanceState)
        mBinding.viewModel = smsInboxViewModel
        mBinding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        addMenuItem()
        observeMessages()
        listeners()
    }

    private fun addMenuItem() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_sms_inbox, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        startActivity(
                            SearchActivity.onNewInstance(
                                context = requireContext(),
                                isGlobalSearch = false,
                                senderAddressId = smsInboxViewModel.senderAddressId
                            )
                        )
                        true
                    }

                    R.id.action_block -> {
                        showYesNoDialog(
                            requireContext(),
                            getString(R.string.block),
                            getString(R.string.blocked_sender_message),
                            {
                                progressDialog.show()
                                smsInboxViewModel.blockSender {
                                    progressDialog.dismiss()
                                    requireActivity().finish()
                                }
                            },
                            {})
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun listeners() {
        smsInboxViewModel.isSendEnabled.observe(viewLifecycleOwner) {
            mBinding.btFragSmsInboxSend.isEnabled = it
        }
        smsInboxViewModel.isScrollDownButtonVisible.observe(viewLifecycleOwner) {
            mBinding.btFragSmsInboxScrollDown.isVisible = it
        }
        mBinding.btFragSmsInboxScrollDown.setOnClickListener {
            mBinding.rvSmsMessages.post {
                mBinding.rvSmsMessages.stopScroll()
                mBinding.rvSmsMessages.smoothScrollToPosition(0)
            }
        }
        mBinding.btFragSmsInboxSend.setOnClickListener {
            if (permissionManager.isDefaultSms()) {
                val message = mBinding.etFragSmsInboxMessage.text.toString()
                mBinding.etFragSmsInboxMessage.text?.clear()
                smsInboxViewModel.sendSms(requireContext(), message)
            } else {
                promptToSetDefaultSmsApp()
            }
        }
        mBinding.ivFragSmsInboxClose.setOnClickListener {
            smsInboxViewModel.clearMessageSelection()
        }
        mBinding.btFragSmsInboxCopy.setOnClickListener {
            copySelectedMessagesToClipboard(requireContext())
            smsInboxViewModel.clearMessageSelection()
        }
        mBinding.btFragSmsInboxDelete.setOnClickListener {
            showYesNoDialog(
                requireContext(),
                getString(R.string.delete),
                getString(R.string.once_deleted_messages_can_t_be_restored),
                {
                    progressDialog.show()
                    smsInboxViewModel.deleteSelectedMessages(requireContext()) {
                        progressDialog.dismiss()
                    }
                },
                {})
        }
        mBinding.btFragSmsInboxReport.setOnClickListener {
            smsInboxViewModel.clearMessageSelection()
            // TODO(API integration to send to remote server, after showing a dialog containing privacy condition.)
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

    private fun promptToSetDefaultSmsApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager =
                requireContext().getSystemService(RoleManager::class.java) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            defaultSmsAppLauncher.launch(intent)
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, requireContext().packageName)
            startActivity(intent)
        }
    }

    private fun fallbackToLegacyIntent() {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, requireContext().packageName)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        mBinding.rvSmsMessages.layoutManager = layoutManager

        _smsInboxAdapter = SmsInboxAdapter(
            smsInboxViewModel.highlightedMessageId, {
                if (smsInboxViewModel.selectedMessages.value.isNotEmpty()) {
                    smsInboxViewModel.toggleMessageSelection(it.data.id)
                }
            }, {
                smsInboxViewModel.toggleMessageSelection(it.data.id)
            }
        )
        mBinding.rvSmsMessages.adapter = smsInboxAdapter

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
                smsInboxViewModel.isAtBottom.value = visibleTop <= 1

                if (topPos != RecyclerView.NO_POSITION) {
                    val item = smsInboxAdapter.currentList.getOrNull(topPos) ?: return
                    val label = when (item) {
                        is SmsInboxListItem.Message -> DateUtils.formatDayHeader(item.data.dateInEpoch)
                        is SmsInboxListItem.Header -> item.header.label
                    }
                    showFloatingDateLabel(label)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                smsInboxViewModel.isRecyclerViewScrolling.value =
                    recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
            }
        })
    }

    private fun showFloatingDateLabel(label: String) {
        mBinding.tvFragSmsInboxFloatingDate.text = label.trim()
        mBinding.tvFragSmsInboxFloatingDate.visibility = View.VISIBLE

        scrollHideRunnable?.let { scrollHandler.removeCallbacks(it) }

        scrollHideRunnable = Runnable {
            mBinding.tvFragSmsInboxFloatingDate.visibility = View.GONE
        }
        scrollHideRunnable?.let {
            scrollHandler.postDelayed(it, DATE_FLOATER_SHOW_TIME)
        }
    }

    private fun observeMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                smsInboxViewModel.messageLoader.collectLatest { loader ->
                    loader?.messages?.collectLatest { list ->
                        smsInboxAdapter.submitList(list, {
                            if (list.isNotEmpty()) {
                                scrollToTargetIfPresent(smsInboxViewModel.targetSmsId)
                            }
                        })
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            smsInboxViewModel.messageLoader
                .filterNotNull()
                .distinctUntilChanged()
                .take(1) // only once
                .collect { loader ->
                    loader.attachScrollListener(
                        mBinding.rvSmsMessages,
                        mBinding.rvSmsMessages.layoutManager as LinearLayoutManager
                    )
                }
        }
    }

    private fun scrollToTargetIfPresent(targetId: Long?) {
        if (smsInboxViewModel.isListInitScrollCalled && !smsInboxViewModel.isAtBottom.value) return

        smsInboxViewModel.isListInitScrollCalled = true
        smsInboxViewModel.targetSmsId = null

        mBinding.rvSmsMessages.post {
            val index = if (targetId == null) 0 else smsInboxAdapter.currentList.indexOfFirst {
                it is SmsInboxListItem.Message && it.data.id == targetId
            }
            if (index != -1) {
                val layoutManager = mBinding.rvSmsMessages.layoutManager as LinearLayoutManager
                layoutManager.scrollToPosition(index)
                targetId?.let {
                    smsInboxViewModel.flashMessage(targetId) {
                        val newIndex = smsInboxAdapter.currentList.indexOfFirst {
                            it is SmsInboxListItem.Message && it.data.id == targetId
                        }
                        if (newIndex != -1) {
                            smsInboxAdapter.notifyItemChanged(newIndex)
                        }
                    }
                    smsInboxAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    private fun copySelectedMessagesToClipboard(context: Context) {
        val messages = smsInboxViewModel.selectedMessages.value
            .sortedBy { it.dateInEpoch } // Optional: keep chronological order
            .joinToString(separator = "\n\n") { it.message } // Double line spacing

        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Selected Sms Messages", messages)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroyView() {
        scrollHideRunnable?.let { scrollHandler.removeCallbacks(it) }
        _smsInboxAdapter = null
        super.onDestroyView()
    }
}
