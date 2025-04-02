package com.summer.notifai.ui.smsinbox.contactlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemSmsContactBinding
import com.summer.notifai.ui.datamodel.ContactMessageInfoDataModel

class ContactListPagingAdapter(
    private val onItemClick: (ContactMessageInfoDataModel) -> Unit
) : PagingDataAdapter<ContactMessageInfoDataModel, ContactListPagingAdapter.ContactViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSmsContactBinding.inflate(inflater, parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ContactViewHolder(
        private val binding: ItemSmsContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactMessageInfoDataModel) {
            binding.model = item
            binding.root.setOnClickListener { onItemClick(item) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ContactMessageInfoDataModel>() {
        override fun areItemsTheSame(
            oldItem: ContactMessageInfoDataModel,
            newItem: ContactMessageInfoDataModel
        ): Boolean = oldItem.rawAddress == newItem.rawAddress

        override fun areContentsTheSame(
            oldItem: ContactMessageInfoDataModel,
            newItem: ContactMessageInfoDataModel
        ): Boolean = oldItem == newItem
    }
}