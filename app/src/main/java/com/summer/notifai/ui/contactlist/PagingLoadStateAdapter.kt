package com.summer.notifai.ui.contactlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.summer.notifai.databinding.ItemLoadStateBinding

class PagingLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadStateBinding.inflate(inflater, parent, false)
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.loadState = loadState
            binding.retryButton.setOnClickListener { retry() }
            binding.executePendingBindings()
        }
    }
}