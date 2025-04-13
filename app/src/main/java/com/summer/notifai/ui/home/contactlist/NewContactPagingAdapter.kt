package com.summer.notifai.ui.home.contactlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemEmptyViewBinding
import com.summer.notifai.databinding.ItemNewContactBinding
import com.summer.notifai.ui.datamodel.NewContactDataModel

class NewContactPagingAdapter(
    private val onItemClick: (NewContactDataModel) -> Unit
) : PagingDataAdapter<NewContactDataModel, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) VIEW_TYPE_PLACEHOLDER else VIEW_TYPE_CONTACT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CONTACT -> {
                val binding = ItemNewContactBinding.inflate(inflater, parent, false)
                ContactViewHolder(binding)
            }

            else -> {
                val binding = ItemEmptyViewBinding.inflate(inflater, parent, false)
                PlaceholderViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactViewHolder -> getItem(position)?.let { holder.bind(it) }
            is PlaceholderViewHolder -> Unit // no-op
        }
    }

    inner class ContactViewHolder(
        private val binding: ItemNewContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewContactDataModel) {
            binding.model = item
            binding.root.setOnClickListener { onItemClick(item) }
            binding.executePendingBindings()
        }
    }

    inner class PlaceholderViewHolder(
        binding: ItemEmptyViewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_CONTACT = 0
        private const val VIEW_TYPE_PLACEHOLDER = 1

        val DiffCallback = object : DiffUtil.ItemCallback<NewContactDataModel>() {
            override fun areItemsTheSame(
                oldItem: NewContactDataModel,
                newItem: NewContactDataModel
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: NewContactDataModel,
                newItem: NewContactDataModel
            ): Boolean = oldItem.id == newItem.id
                    && oldItem.phoneNumber == newItem.phoneNumber
        }
    }
}