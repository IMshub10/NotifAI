package com.summer.notifai.ui.inbox.smsMessages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemReceivedSmsMessageBinding
import com.summer.notifai.databinding.ItemSentSmsMessageBinding
import com.summer.notifai.databinding.ItemSmsDateHeaderBinding
import com.summer.notifai.ui.datamodel.SmsInboxListItem

class SmsInboxPagingAdapter(
    private val onItemClick: (SmsInboxListItem.Message) -> Unit
) : PagingDataAdapter<SmsInboxListItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_RECEIVED_MESSAGE = 1
        private const val VIEW_TYPE_SENT_MESSAGE = 2

        private val DiffCallback = object : DiffUtil.ItemCallback<SmsInboxListItem>() {
            override fun areItemsTheSame(
                oldItem: SmsInboxListItem,
                newItem: SmsInboxListItem
            ): Boolean {
                return when {
                    oldItem is SmsInboxListItem.Message && newItem is SmsInboxListItem.Message ->
                        oldItem.data.id == newItem.data.id

                    oldItem is SmsInboxListItem.Header && newItem is SmsInboxListItem.Header ->
                        oldItem.header.label == newItem.header.label

                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: SmsInboxListItem,
                newItem: SmsInboxListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = peek(position)
        return when (model) {
            is SmsInboxListItem.Header -> VIEW_TYPE_HEADER
            is SmsInboxListItem.Message -> if (model.data.isIncoming) VIEW_TYPE_RECEIVED_MESSAGE else VIEW_TYPE_SENT_MESSAGE
            else -> throw IllegalStateException("Unknown item type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemSmsDateHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED_MESSAGE -> {
                val binding = ItemReceivedSmsMessageBinding.inflate(inflater, parent, false)
                ReceivedMessageViewHolder(binding)
            }

            VIEW_TYPE_SENT_MESSAGE -> {
                val binding = ItemSentSmsMessageBinding.inflate(inflater, parent, false)
                SentMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SmsInboxListItem.Message -> if (item.data.isIncoming) (holder as ReceivedMessageViewHolder).bind(item) else (holder as SentMessageViewHolder).bind(item)
            is SmsInboxListItem.Header -> (holder as HeaderViewHolder).bind(item)
            null -> Unit
        }
    }

    inner class ReceivedMessageViewHolder(
        private val binding: ItemReceivedSmsMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmsInboxListItem.Message) {
            binding.model = item.data
            binding.root.setOnClickListener { onItemClick(item) }
            binding.executePendingBindings()
        }
    }

    inner class SentMessageViewHolder(
        private val binding: ItemSentSmsMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmsInboxListItem.Message) {
            binding.model = item.data
            binding.root.setOnClickListener { onItemClick(item) }
            binding.executePendingBindings()
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemSmsDateHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmsInboxListItem.Header) {
            binding.label = item.header.label
            binding.executePendingBindings()
        }
    }
}