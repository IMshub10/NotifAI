package com.summer.notifai.ui.onboarding.permissions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemPermissionBinding
import com.summer.notifai.ui.datamodel.PermissionItemModel

class PermissionsAdapter(private val items: List<PermissionItemModel>) :
    ListAdapter<PermissionItemModel, PermissionsAdapter.PermissionViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PermissionViewHolder(
        ItemPermissionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PermissionViewHolder(private val binding: ItemPermissionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PermissionItemModel) {
            binding.model = item
            binding.executePendingBindings()
        }
    }

    internal class DiffUtilCallback : DiffUtil.ItemCallback<PermissionItemModel>() {
        override fun areItemsTheSame(
            oldItem: PermissionItemModel,
            newItem: PermissionItemModel,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PermissionItemModel,
            newItem: PermissionItemModel,
        ): Boolean {
            return oldItem.icon == newItem.icon && oldItem.title == newItem.title && oldItem.description == newItem.description
        }
    }
}