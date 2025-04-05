package com.summer.notifai.ui.inbox

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemSmsMessageBinding
import com.summer.notifai.databinding.ItemSmsDateHeaderBinding
import com.summer.notifai.ui.datamodel.SmsInboxListItem

class SmsInboxPagingAdapter(
    private val onItemClick: (SmsInboxListItem.Message) -> Unit
) : PagingDataAdapter<SmsInboxListItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_MESSAGE = 0
        private const val VIEW_TYPE_HEADER = 1

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
        return when (peek(position)) {
            is SmsInboxListItem.Header -> VIEW_TYPE_HEADER
            is SmsInboxListItem.Message -> VIEW_TYPE_MESSAGE
            else -> throw IllegalStateException("Unknown item type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_MESSAGE -> {
                val binding = ItemSmsMessageBinding.inflate(inflater, parent, false)
                MessageViewHolder(binding)
            }

            VIEW_TYPE_HEADER -> {
                val binding = ItemSmsDateHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SmsInboxListItem.Message -> (holder as MessageViewHolder).bind(item)
            is SmsInboxListItem.Header -> (holder as HeaderViewHolder).bind(item)
            null -> Unit
        }
    }

    inner class MessageViewHolder(
        private val binding: ItemSmsMessageBinding
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