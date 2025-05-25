package com.summer.notifai.ui.settings.blocklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.core.data.local.model.ContactInfoInboxModel
import com.summer.notifai.databinding.ItemBlockedSenderBinding
import com.summer.notifai.databinding.ItemEmptyViewBinding

class BlockListAdapter(
    private val onItemClick: (ContactInfoInboxModel) -> Unit,
    private val onLongItemClick: (ContactInfoInboxModel) -> Unit
) : PagingDataAdapter<ContactInfoInboxModel, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) VIEW_TYPE_PLACEHOLDER else VIEW_TYPE_BLOCKED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BLOCKED -> {
                val binding = ItemBlockedSenderBinding.inflate(inflater, parent, false)
                BlockedSenderViewHolder(binding)
            }

            else -> {
                val binding = ItemEmptyViewBinding.inflate(inflater, parent, false)
                PlaceholderViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BlockedSenderViewHolder -> getItem(position)?.let { holder.bind(it) }
            is PlaceholderViewHolder -> Unit // no-op
        }
    }

    inner class BlockedSenderViewHolder(
        private val binding: ItemBlockedSenderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactInfoInboxModel) {
            binding.model = item
            binding.root.setOnClickListener { onItemClick(item) }
            binding.root.setOnLongClickListener {
                onLongItemClick(item)
                return@setOnLongClickListener true
            }
            binding.executePendingBindings()
        }
    }

    inner class PlaceholderViewHolder(
        binding: ItemEmptyViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_BLOCKED = 0
        private const val VIEW_TYPE_PLACEHOLDER = 1

        val DiffCallback = object : DiffUtil.ItemCallback<ContactInfoInboxModel>() {
            override fun areItemsTheSame(
                oldItem: ContactInfoInboxModel,
                newItem: ContactInfoInboxModel
            ): Boolean = oldItem.senderAddressId == newItem.senderAddressId

            override fun areContentsTheSame(
                oldItem: ContactInfoInboxModel,
                newItem: ContactInfoInboxModel
            ): Boolean =
                oldItem.senderName == newItem.senderName &&
                        oldItem.phoneNumber == newItem.phoneNumber &&
                        oldItem.senderType == newItem.senderType
        }
    }
}